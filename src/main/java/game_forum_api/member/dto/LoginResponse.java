package game_forum_api.member.dto;

import game_forum_api.member.model.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

	private String token;

	private Member member;
}
