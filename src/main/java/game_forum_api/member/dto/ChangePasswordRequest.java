package game_forum_api.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

	@NotBlank(message = "不能為空")
	private String oldPassword;

	@NotBlank(message = "不能為空")
	private String newPassword;
}
