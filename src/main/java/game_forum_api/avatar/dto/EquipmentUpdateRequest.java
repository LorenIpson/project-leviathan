package game_forum_api.avatar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentUpdateRequest {
    private String type;         // 裝備類型 (clothes, weapon等)
    private Integer commodityId; // 要裝備的商品ID (null表示卸載該類型所有裝備)
}