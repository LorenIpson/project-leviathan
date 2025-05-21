package game_forum_api.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequest {

	@NotBlank(message = "不能為空")
	private String username;

	private String phone;

	private String address;

	private byte[] photo;
}
