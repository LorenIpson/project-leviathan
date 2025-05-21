package game_forum_api.member.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.auth.oauth2.TokenVerifier;
import com.google.auth.oauth2.TokenVerifier.VerificationException;

import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.member.dto.GoogleAccount;
import game_forum_api.member.dto.RegisterRequest;
import game_forum_api.member.dto.TokenRequest;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GoogleAuthService {

	private static final String GOOGLE_CLIENT_ID = "1092356241064-vi45pdqq54vf093tmn3jom39pi2f5k5g.apps.googleusercontent.com";

	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private MemberRepository memberRepository;

	public GoogleAccount authenticateWithGoogle(TokenRequest request) {
		try {
			TokenVerifier verifier = TokenVerifier.newBuilder().setAudience(GOOGLE_CLIENT_ID).build();

			JsonWebSignature idToken = verifier.verify(request.getToken());

			if (idToken != null) {
				System.out.println(idToken);
				String name = idToken.getPayload().get("name").toString();
				String email = idToken.getPayload().get("email").toString();

				GoogleAccount googleAccount = new GoogleAccount();
				googleAccount.setName(name);
				googleAccount.setEmail(email);
				return googleAccount;
			}
		} catch (VerificationException e) {
			throw new UnauthorizedException("無效的 google token");
		}
		throw new UnauthorizedException("登入失敗");
	}

	public Member googleLogin(GoogleAccount googleAccount) {
		String username = googleAccount.getName();
		String accountId = googleAccount.getEmail(); // google 帳號註冊後使用 email 作為 accountId

		Optional<Member> memberOpt = memberRepository.findByAccountId(accountId);
		Member member = null;

		if (memberOpt.isEmpty()) {
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setAccountId(accountId);
			registerRequest.setUsername(username);
			registerRequest.setEmail(accountId);
			member = memberAuthService.register(registerRequest, false);

		} else {
			member = memberOpt.get();
		}

		memberAuthService.googleLogin(member);

		return member;
	}
}
