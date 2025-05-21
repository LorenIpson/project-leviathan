package game_forum_api.avatar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseGiftRequest {

	private Integer recipientId;

	private Integer commodityId;
}
