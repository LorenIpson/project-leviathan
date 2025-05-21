package game_forum_api.avatar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AvatarCommodityRequest {

    @NotBlank(message = "商品名稱不能為空")
    private String commodityName;

    @NotBlank(message = "商品類型不能為空")
    private String type;

    @NotBlank(message = "圖片路徑不能為空")
    private String photoPath;

    @NotNull(message = "上架時間不能為空")
    private Date shelfTime;

    @NotNull(message = "商品價格不能為空")
    private Integer point;
}