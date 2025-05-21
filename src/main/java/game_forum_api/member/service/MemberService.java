package game_forum_api.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.jwt.JwtService;
import game_forum_api.member.dto.UpdateMemberRequest;
import game_forum_api.member.dto.UpdateMemberRoleRequest;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	public Member findById(Integer id) {

		return memberRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("找不到會員 ID：" + id + " 的資料"));
	}

	public Integer findMemberRoleById(Integer id) {

		return memberRepository.findRoleById(id).orElseThrow(() -> new ResourceNotFoundException("找不到用戶角色"));
	}

	public Boolean checkAccountExistence(String accountId) {

		return memberRepository.findByAccountId(accountId).isPresent();
	}

	public List<Member> searchRole1Members(String searchTerm) {
		return memberRepository.searchRole1ByUsernameOrAccountId(searchTerm);
	}

	public List<Member> searchMembers(String searchTerm) {
		return memberRepository.searchByUsernameOrAccountId(searchTerm);
	}

	public List<Member> findByRole(Integer role) {
		return memberRepository.findByRole(role);
	}

	public String updateMemberRole(UpdateMemberRoleRequest request) {

		Integer memberId = request.getMemberId();
		Integer role = request.getRole();

		Member member = findById(memberId);
		member.setRole(role);
		memberRepository.save(member);

		// TODO 加入被更新者的通知

		return "更新成功";
	}

	public String updateMember(Integer memberId, UpdateMemberRequest request) {

		String username = request.getUsername();
		String phone = request.getPhone();
		String address = request.getAddress();
		byte[] photo = request.getPhoto();

		Member member = findById(memberId);
		member.setUsername(username);
		member.setPhone(phone);
		member.setAddress(address);
		member.setPhoto(photo);

		memberRepository.save(member);

		return "更新成功";
	}
}
