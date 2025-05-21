package game_forum_api.coupons.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import game_forum_api.cart.domain.ShoppingCart;
import game_forum_api.cart.dto.ShoppingCartResponse;
import game_forum_api.coupons.domain.DiscountCoupons;
import game_forum_api.coupons.dto.DiscountCouponsRequest;
import game_forum_api.coupons.dto.DiscountCouponsResponse;
import game_forum_api.coupons.repository.DiscountCouponsRepository;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.merchant.domain.Merchants;
import game_forum_api.merchant.repository.MerchantRepository;
import game_forum_api.notification.dto.NewNotification;
import game_forum_api.notification.service.NotificationService;

@Service
public class DiscountCouponsService {

    private final DiscountCouponsRepository discountCouponsRepository;
    private final MemberRepository memberRepository;
    private final MerchantRepository merchantRepository;
    private final NotificationService notificationService;

    public DiscountCouponsService(DiscountCouponsRepository discountCouponsRepository, MemberRepository memberRepository,MerchantRepository merchantRepository,NotificationService notificationService) {
        this.discountCouponsRepository = discountCouponsRepository;
        this.memberRepository = memberRepository;
        this.merchantRepository = merchantRepository;
        this.notificationService = notificationService;
    }

    // 新增折扣券
    public DiscountCouponsResponse createDiscountCoupon(DiscountCouponsRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日HH點mm分");
        
        DiscountCoupons coupon = new DiscountCoupons();
        coupon.setCode(request.getCode());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setStatus(request.getStatus());
        coupon.setMember(member);

        DiscountCoupons savedCoupon = discountCouponsRepository.save(coupon);
    	
    	// 建立新增收到優惠券通知
    	NewNotification notification = new NewNotification();
    	notification.setMemberId(member.getId());
    	notification.setType("coupon");
    	notification.setValue(member.getId().toString());
    	notification.setMessage("已收到一張優惠券，到期日為："+coupon.getExpiryDate().format(formatter)+" 請盡快使用~ ");
    	notificationService.createNotification(notification);

        return new DiscountCouponsResponse(
                savedCoupon.getCouponId(),
                savedCoupon.getCode(),
                savedCoupon.getDiscountPercentage(),
                savedCoupon.getExpiryDate(),
                savedCoupon.getStatus(),
                savedCoupon.getMember().getUsername()
        );
    }

    // ID查詢
    public DiscountCouponsResponse getDiscountCouponById(Integer couponId) {
        DiscountCoupons coupon = discountCouponsRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到折扣券"));

        return new DiscountCouponsResponse(
                coupon.getCouponId(),
                coupon.getCode(),
                coupon.getDiscountPercentage(),
                coupon.getExpiryDate(),
                coupon.getStatus(),
                coupon.getMember().getUsername()
        );
    }
    
    // 取得特定會員的折價券清單
    public List<DiscountCouponsResponse> getCouponByMemberId(Integer memberId) {
        List<DiscountCoupons> coupons = discountCouponsRepository.findByMemberId(memberId);
        
        return coupons.stream().map(coupon -> 
        new DiscountCouponsResponse(
        	coupon.getCouponId(),
        	coupon.getCode(),
        	coupon.getDiscountPercentage(),
        	coupon.getExpiryDate(),
        	coupon.getStatus(),
        	coupon.getMember().getUsername()
        )).toList();
    }

    // 更新折扣券
    public DiscountCouponsResponse updateDiscountCoupon(Integer couponId, DiscountCouponsRequest request) {
        DiscountCoupons coupon = discountCouponsRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到折扣券"));

        coupon.setCode(request.getCode());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setStatus(request.getStatus());

        DiscountCoupons updatedCoupon = discountCouponsRepository.save(coupon);

        return new DiscountCouponsResponse(
                updatedCoupon.getCouponId(),
                updatedCoupon.getCode(),
                updatedCoupon.getDiscountPercentage(),
                updatedCoupon.getExpiryDate(),
                updatedCoupon.getStatus(),
                updatedCoupon.getMember().getUsername()
        );
    }
    
    public DiscountCouponsResponse markAsUsed(Integer couponId, String username) {
        DiscountCoupons coupon = discountCouponsRepository.findById(couponId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到折扣券"));

        if (!coupon.getMember().getUsername().equals(username)) {
            throw new AccessDeniedException("無權限使用此折扣券");
        }

        if (!"unused".equals(coupon.getStatus())) {
            throw new IllegalStateException("折扣券已使用或無效");
        }

        coupon.setStatus("used");
        DiscountCoupons updated = discountCouponsRepository.save(coupon);

        return new DiscountCouponsResponse(
            updated.getCouponId(),
            updated.getCode(),
            updated.getDiscountPercentage(),
            updated.getExpiryDate(),
            updated.getStatus(),
            updated.getMember().getUsername()
        );
    }
    
    public void validateAndUseCoupon(Integer couponId, Integer memberId) {
        DiscountCoupons coupon = discountCouponsRepository.findById(couponId)
            .orElseThrow(() -> new ResourceNotFoundException("折價券不存在"));

        if (!coupon.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("你無權使用這張折價券");
        }

        if (!"unused".equalsIgnoreCase(coupon.getStatus())) {
            throw new IllegalStateException("折價券已被使用或無效");
        }

        coupon.setStatus("used");
        discountCouponsRepository.save(coupon);
    }

    // 刪除折扣券
    public void deleteDiscountCoupon(Integer couponId) {
        DiscountCoupons coupon = discountCouponsRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到折扣券"));
        discountCouponsRepository.delete(coupon);
    }
}
