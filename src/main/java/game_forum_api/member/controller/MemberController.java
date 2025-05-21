package game_forum_api.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.CheckMemberRole;
import game_forum_api.annotation.MemberId;
import game_forum_api.jwt.JwtService;
import game_forum_api.member.dto.ChangePasswordRequest;
import game_forum_api.member.dto.RegisterRequest;
import game_forum_api.member.dto.UpdateMemberRequest;
import game_forum_api.member.dto.UpdateMemberRoleRequest;
import game_forum_api.member.dto.GetMemberBySearchTermRequest;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberAuthService;
import game_forum_api.member.service.MemberService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/members")
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberAuthService memberAuthService;

	@GetMapping("/{id}")
	public ResponseEntity<Member> findById(@PathVariable Integer id) {

		Member member = memberService.findById(id);
		return ResponseEntity.ok(member);
	}

	@CheckMemberRole(2)
	@PostMapping("/getCommonBySearchTerm")
	public ResponseEntity<List<Member>> getRole1MemberBySearchTerm(@RequestBody GetMemberBySearchTermRequest request) {

		List<Member> memberList = memberService.searchRole1Members(request.getSearchTerm());
		return ResponseEntity.ok(memberList);
	}

	@PostMapping("/getBySearchTerm")
	public ResponseEntity<List<Member>> getMemberBySearchTerm(@RequestBody GetMemberBySearchTermRequest request) {

		List<Member> memberList = memberService.searchMembers(request.getSearchTerm());
		return ResponseEntity.ok(memberList);
	}

	@PostMapping("/getByToken")
	public ResponseEntity<Member> getMemberByToken(@MemberId Integer memberId) {

		Member member = memberService.findById(memberId);
		return ResponseEntity.ok(member);
	}

	@PostMapping("/register")
	public ResponseEntity<Member> register(@Valid @RequestBody RegisterRequest request) {

		Member member = memberAuthService.register(request, true);
		return ResponseEntity.ok(member);
	}

	@PutMapping("/changePassword")
	public ResponseEntity<String> changePassword(@MemberId Integer memberId,
			@Valid @RequestBody ChangePasswordRequest request) {

		String result = memberAuthService.changePassword(memberId, request);
		return ResponseEntity.ok(result);
	}

	@CheckMemberRole(2)
	@PutMapping("/updateMemberRole")
	public ResponseEntity<String> updateMemberRole(@Valid @RequestBody UpdateMemberRoleRequest request) {

		String result = memberService.updateMemberRole(request);
		return ResponseEntity.ok(result);
	}

	@CheckMemberRole(2)
	@GetMapping("/findByRole/{role}")
	public ResponseEntity<List<Member>> findByRole(@PathVariable Integer role) {

		List<Member> memberlist = memberService.findByRole(role);
		return ResponseEntity.ok(memberlist);
	}

	// 超級管理員(權限3)用來指派或移除會員管理員權限(權限2)
	@CheckMemberRole(3)
	@PutMapping("/updateAdminPermission")
	public ResponseEntity<String> updateAdminPermission(@Valid @RequestBody UpdateMemberRoleRequest request) {

		String result = memberService.updateMemberRole(request);
		return ResponseEntity.ok(result);
	}

	@PutMapping
	public ResponseEntity<String> updateMember(@MemberId Integer memberId,
			@Valid @RequestBody UpdateMemberRequest request) {

		String result = memberService.updateMember(memberId, request);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/checkAccountExistence/{accountId}")
	public ResponseEntity<Boolean> checkAccountExistence(@PathVariable String accountId) {

		return ResponseEntity.ok(memberService.checkAccountExistence(accountId));
	}

	// 此為範例，請勿引用
	// 透過 header 的 X-Member-Id 取得 memberId 範例
	@GetMapping("/getMemberIdByHeader")
	public ResponseEntity<String> getMemberIdByHeader(@RequestHeader("X-Member-Id") Integer memberId) {

		return ResponseEntity.ok("Member ID: " + memberId);
	}

	// 此為範例，請勿引用
	// 透過 header 的 Authorization 取得 memberId 範例
	// 可直接使用定義好的 resolver 使用 @MemberId Integer memberId 即可
	@GetMapping("/getMemberIdByAuthorization")
	public ResponseEntity<String> getMemberId(@MemberId Integer memberId) {

		return ResponseEntity.ok("Member ID: " + memberId);
	}

	// 此為範例，請勿引用
	// 透過 header 的 Authorization 取得 memberRole 範例
	@GetMapping("/getMemberRoleByAuthorization")
	public ResponseEntity<String> getMemberRole(@MemberId Integer memberId) {

		Integer memberRole = memberService.findMemberRoleById(memberId);
		return ResponseEntity.ok("Member Role: " + memberRole);
	}

}
