package game_forum_api.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLogByPagiationRequest {

	@NotNull(message = "不能為空")
	private Integer pageNumber;

	@NotNull(message = "不能為空")
	private Integer pageRow;
}
