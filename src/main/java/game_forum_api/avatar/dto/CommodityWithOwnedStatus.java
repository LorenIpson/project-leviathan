package game_forum_api.avatar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommodityWithOwnedStatus {

	private Integer commodityId;
	private String commodityName;
	private String type;
	private String photoPath;
	private Integer point;
	private Date shelfTime;
	private Integer equipmentStatus;
}
