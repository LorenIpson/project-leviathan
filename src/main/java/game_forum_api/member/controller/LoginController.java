package game_forum_api.member.controller;

import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.jwt.JsonWebTokenUtility;
import game_forum_api.member.dto.GoogleAccount;
import game_forum_api.member.dto.LoginRequest;
import game_forum_api.member.dto.LoginResponse;
import game_forum_api.member.dto.TokenRequest;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.GoogleAuthService;
import game_forum_api.member.service.MemberAuthService;
import jakarta.validation.Valid;

@RestController
public class LoginController {

	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;

	@Autowired
	private GoogleAuthService googleAuthService;

	@PostMapping("/api/secure/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse res = new LoginResponse();

		Member member = memberAuthService.login(request);
		res.setMember(member);

		// JWT
		String token = getJwtToken(member);
		res.setToken(token);

		return ResponseEntity.ok(res);
	}

	@PostMapping("/api/secure/google-login")
	public ResponseEntity<LoginResponse> authenticateWithGoogle(@RequestBody TokenRequest request) {
		LoginResponse res = new LoginResponse();

		GoogleAccount googleAccount = googleAuthService.authenticateWithGoogle(request);
		Member member = googleAuthService.googleLogin(googleAccount);
		res.setMember(member);

		// JWT
		String token = getJwtToken(member);
		res.setToken(token);

		return ResponseEntity.ok(res);
	}

	@PostMapping("/api/secure/logout")
	public ResponseEntity<String> logout(@MemberId Integer memberId) {
		String result = memberAuthService.logout(memberId);
		return ResponseEntity.ok(result);
	}

	// 測試用，禁止引用
	@PostMapping("api/secure/getencodePassword")
	public ResponseEntity<String> getencodePassword(@RequestBody String password) {
		String encodePassword = memberAuthService.encodePassword(password);
		return ResponseEntity.ok(encodePassword);
	}

	// 測試用，禁止引用
	@PostMapping("api/secure/verifyPassword")
	public ResponseEntity<Boolean> verifyPassword(@RequestBody String encodePassword) {
		boolean matches = memberAuthService.passwordMatches("1234", encodePassword);
		return ResponseEntity.ok(matches);
	}

	private String getJwtToken(Member member) {
		Integer id = member.getId();
		String accountId = member.getAccountId();
		String email = member.getEmail();
		Date birthdate = member.getBirthdate();
		JSONObject memberInfo = new JSONObject().put("id", id).put("accountId", accountId).put("email", email)
				.put("birthdate", birthdate);

		return jsonWebTokenUtility.createToken(memberInfo.toString());
	}
}
