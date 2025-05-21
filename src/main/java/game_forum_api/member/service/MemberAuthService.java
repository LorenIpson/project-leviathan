package game_forum_api.member.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import game_forum_api.avatar.model.AvatarPhoto;
import game_forum_api.avatar.model.AvatarStorehouse;
import game_forum_api.avatar.repository.AvatarPhotoRepository;
import game_forum_api.avatar.repository.AvatarStorehouseRepository;
import game_forum_api.coupons.dto.DiscountCouponsRequest;
import game_forum_api.coupons.service.DiscountCouponsService;
import game_forum_api.coupons.utils.RandomCodeGenerator;
import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.member.dto.ChangePasswordRequest;
import game_forum_api.member.dto.LoginRequest;
import game_forum_api.member.dto.RegisterRequest;
import game_forum_api.member.model.Member;
import game_forum_api.member.model.MemberAuth;
import game_forum_api.member.model.MemberLog;
import game_forum_api.member.repository.MemberAuthRepository;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.notification.service.NotificationService;
import game_forum_api.points.model.Points;
import game_forum_api.points.repository.PointsRepository;
import game_forum_api.points.service.PointsService;

@Service
public class MemberAuthService {

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberAuthRepository memberAuthRepository;

	@Autowired
	private PointsRepository pointsRepository;

	@Autowired
	private AvatarPhotoRepository avatarPhotoRepository;
	
	@Autowired
	private AvatarStorehouseRepository avatarStorehouseRepository;

	@Autowired
	private MemberLogService memberLogService;

	@Autowired
	private PointsService pointsService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private DiscountCouponsService DiscountCouponsService;

	// 添加 noRollbackFor 避免登入失敗的時候交易回溯(記 log，更新 memberAuth 等...)
	@Transactional(noRollbackFor = UnauthorizedException.class)
	public Member login(LoginRequest request) {

		String accountId = request.getAccountId();
		String password = request.getPassword();

		// 檢查帳號是否存在
		Member member = memberRepository.findByAccountId(accountId)
				.orElseThrow(() -> new UnauthorizedException("帳號密碼錯誤"));
		MemberAuth memberAuth = memberAuthRepository.findById(member.getId()).orElseThrow(() -> {
			return new ResourceNotFoundException("異常，該會員 memberAuth 不存在");
		});
		Integer memberId = member.getId();
		String encodePassword = memberAuth.getPassword();
		LocalDateTime lockTime = memberAuth.getLockTime();
		Integer failedAttempts = memberAuth.getFailedAttempts();
		LocalDateTime lastLogin = memberAuth.getLastLogin();

		// 檢查是否處於鎖定中
		if (isAccountLocked(lockTime, memberId)) {
			long minutesRemaining = Duration.between(LocalDateTime.now(), lockTime).toSeconds();
			String errorMsg = "連續登入錯誤，帳號已遭鎖定，請於 " + minutesRemaining + " 秒後再進行登入";
			throw new UnauthorizedException(errorMsg);
		}

		// 檢查密碼是否正確
		if (!passwordMatches(password, encodePassword)) {
			System.out.println("============failedAttempts：" + failedAttempts);
			failedAttempts++;
			LocalDateTime newLockTime = (failedAttempts == 3) ? LocalDateTime.now().plusMinutes(5) : null;
			updateFailedAttempts(memberId, newLockTime);
			setMemberLog(memberId, "登入失敗");
			throw new UnauthorizedException("帳號密碼錯誤");
		}

		// 檢查是否今日首次登入
		if (lastLogin == null || isFirstLoginToday(lastLogin)) {
			// 當日首次登入回饋
			pointsService.updatePoints(memberId, 30, "每日登入回饋");
			notificationService.createNotification(memberId, "points", "每日登入，獲得 30 點數", null);
		}

		memberAuthRepository.resetFailedAttemptsAndLockTime(memberId);
		memberAuthRepository.updateLastLogin(memberId, LocalDateTime.now());
		setMemberLog(memberId, "登入成功");
		return member;

	}

	public Member googleLogin(Member member) {
		Integer memberId = member.getId();
		MemberAuth memberAuth = memberAuthRepository.findById(member.getId()).orElseThrow(() -> {
			return new ResourceNotFoundException("異常，該會員 memberAuth 不存在");
		});
		LocalDateTime lastLogin = memberAuth.getLastLogin();

		// 檢查是否今日首次登入
		if (lastLogin == null || isFirstLoginToday(lastLogin)) {
			// 當日首次登入回饋
			pointsService.updatePoints(memberId, 30, "每日登入回饋");
			notificationService.createNotification(memberId, "points", "每日登入，獲得 30 點數", null);
		}
		memberAuthRepository.updateLastLogin(memberId, LocalDateTime.now());
		return member;
	}

	private boolean isAccountLocked(LocalDateTime lockTime, Integer memberId) {
		if (lockTime != null) {
			if (lockTime.isBefore(LocalDateTime.now())) {
				memberAuthRepository.resetFailedAttemptsAndLockTime(memberId);
				return false; // 帳號解鎖
			}
			return true; // 帳號仍然被鎖定
		}
		return false; // 未被鎖定
	}

	private void updateFailedAttempts(Integer memberId, LocalDateTime lockTime) {
		if (lockTime != null) {
			memberAuthRepository.updateLockTime(memberId, lockTime);
		}
		memberAuthRepository.incrementFailedAttempts(memberId);
	}

	private boolean isFirstLoginToday(LocalDateTime lastLogin) {
		return !lastLogin.toLocalDate().equals(LocalDate.now());
	}

	public String logout(Integer memberId) {
		setMemberLog(memberId, "登出成功");
		return "登出成功";
	}

	@Transactional
	public Member register(RegisterRequest request, Boolean isOfficeAccount) {
		String accountId = request.getAccountId();
		String password = request.getPassword();
		String username = request.getUsername();
		String email = request.getEmail();
		String phone = request.getPhone();
		String address = request.getAddress();
		Date birthdate = request.getBirthdate();
		byte[] photo = request.getPhoto();

		Optional<Member> memberOpt = memberRepository.findByAccountId(accountId);
		if (memberOpt.isPresent()) {
			throw new DataConflictException("帳號已存在");
		}

		// 會員個人資料
		Member newMember = new Member();
		newMember.setAccountId(accountId);
		newMember.setUsername(username);
		newMember.setEmail(email);
		newMember.setPhone(phone);
		newMember.setAddress(address);
		newMember.setBirthdate(birthdate);
		newMember.setPhoto(photo);
		newMember.setRole(1);
		newMember.setCreatedAt(new Date());
		Member member = memberRepository.save(newMember);

		// 會員密碼
		MemberAuth newMemberAuth = new MemberAuth();
		newMemberAuth.setId(member.getId());
		newMemberAuth.setOfficialAccount(isOfficeAccount ? 1 : 0);
		newMemberAuth.setPassword(isOfficeAccount ? encodePassword(password) : null);
		newMemberAuth.setFailedAttempts(0);
		memberAuthRepository.save(newMemberAuth);

		// 會員點數
		Points points = new Points();
		points.setMemberId(member.getId());
		points.setPoints(0);
		pointsRepository.save(points);

		// 會員頭像
		AvatarPhoto avatarPhoto = new AvatarPhoto();
		avatarPhoto.setMemberId(member.getId());
		avatarPhotoRepository.save(avatarPhoto);
		
		// 初始裝備(送一件黑色背景(預設已裝備中)
		AvatarStorehouse avatarStorehouse = new AvatarStorehouse();
		avatarStorehouse.setMemberId(member.getId());
		avatarStorehouse.setCommodityId(2); // 黑色背景
		avatarStorehouse.setEquipmentStatus(1); // 裝備狀態
		avatarStorehouseRepository.save(avatarStorehouse);

		setMemberLog(member.getId(), "帳號註冊成功");

		setRegisterBonus(member.getId());

		return member;
	}

	@Transactional
	public String changePassword(Integer memberId, ChangePasswordRequest request) {

		String oldPassword = request.getOldPassword();
		String newPassword = request.getNewPassword();

		MemberAuth memberAuth = memberAuthRepository.findById(memberId)
				.orElseThrow(() -> new UnauthorizedException("查無該會員資料"));
		if (!passwordMatches(oldPassword, memberAuth.getPassword())) {
			setMemberLog(memberId, "更新密碼失敗");
			throw new UnauthorizedException("帳號密碼錯誤");
		}

		// 設置新密碼
		memberAuth.setPassword(encodePassword(newPassword));
		memberAuthRepository.save(memberAuth);
		setMemberLog(memberId, "更新密碼成功");
		return "修改成功";

	}

	private void setRegisterBonus(Integer memberId) {

		// 新增點數
		pointsService.updatePoints(memberId, 50, "註冊回饋");
		notificationService.createNotification(memberId, "points", "恭喜您註冊成功，獲得 50 點數", null);

		// 新增優惠券
		DiscountCouponsRequest coupon = new DiscountCouponsRequest();
		coupon.setMemberId(memberId);
		coupon.setCode(RandomCodeGenerator.generateRandomCode(16));
		coupon.setDiscountPercentage(new BigDecimal(80));
		coupon.setExpiryDate(LocalDateTime.now().plusMonths(3));
		coupon.setStatus("unused");
		DiscountCouponsService.createDiscountCoupon(coupon);
		notificationService.createNotification(memberId, "coupon", "恭喜您註冊成功，獲得商城優惠券", null);

	}

	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean passwordMatches(String password, String encodedPassword) {
		return passwordEncoder.matches(password, encodedPassword);
	}

	private void setMemberLog(Integer memberId, String action) {
		MemberLog memberLog = new MemberLog();
		memberLog.setMemberId(memberId);
		memberLog.setAction(action);
		memberLogService.createMemberLog(memberLog);
	}

}
