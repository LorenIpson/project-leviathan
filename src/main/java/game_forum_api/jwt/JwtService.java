package game_forum_api.jwt;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.member.service.MemberService;

@Service
public class JwtService {

	@Autowired
	private MemberService memberService;

	@Autowired
	private JsonWebTokenUtility jsonWebTokenUtility;

	// 傳送 request header 的 Authorization 來驗證並取得該使用者 ID
	public Integer getMemberIdByHeaderToken(String authorizationHeader) {

		String subject = verifyHeaderToken(authorizationHeader);
		if (subject == null) {
			throw new UnauthorizedException("token 驗證失敗");
		}

		JSONObject memberInfo = new JSONObject(subject);
		if (!memberInfo.has("id")) {
			throw new ResourceNotFoundException("無法從 token 取得用戶 ID");
		}

		return memberInfo.getInt("id");
	}

	// 傳送 request header 的 Authorization 來驗證並取得該使用者權限
	// 權限等級（0: 停權, 1: 一般會員, 2: 管理員, 3: 最高管理員）
	public Integer getMemberRoleByHeaderToken(String authorizationHeader) {

		Integer memberId = getMemberIdByHeaderToken(authorizationHeader);
		return memberService.findMemberRoleById(memberId);
	}

	private String verifyHeaderToken(String authorizationHeader) {

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7); // 去掉 "Bearer " 前綴
			String subject = jsonWebTokenUtility.validateToken(token);

			return subject;
		}

		throw new BadRequestException("authorizationHeader 解析錯誤");
	}

}
