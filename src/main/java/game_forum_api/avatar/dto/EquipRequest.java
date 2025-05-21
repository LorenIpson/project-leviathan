package game_forum_api.avatar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipRequest {

    @NotNull(message = "商品ID不能為空")
    private Integer commodityId; // 商品ID
}