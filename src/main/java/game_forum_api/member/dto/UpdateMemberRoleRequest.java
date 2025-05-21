package game_forum_api.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRoleRequest {

	@NotNull(message = "不能為空")
	private Integer memberId;

	@NotNull(message = "不能為空")
	private Integer role;
}
