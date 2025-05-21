package game_forum_api.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank(message = "不能為空")
	private String accountId;

	@NotBlank(message = "不能為空")
	private String password;
}
