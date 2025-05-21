package game_forum_api.avatar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquippedItemDTO {
    private Integer storehouseId;     // 倉庫記錄ID
    private Integer commodityId;     // 商品ID
    private String type;            // 商品類型(background/weapon等)
    private String commodityName;    // 商品名稱
    private String photoPath;       // 圖片路徑
    private Integer equipmentStatus; // 裝備狀態

    // 可選：添加構造方法方便Repository使用
    public EquippedItemDTO(Integer storehouseId, Integer commodityId, String type, 
                          String commodityName, String photoPath, Integer equipmentStatus) {
        this.storehouseId = storehouseId;
        this.commodityId = commodityId;
        this.type = type;
        this.commodityName = commodityName;
        this.photoPath = photoPath;
        this.equipmentStatus = equipmentStatus;
    }
}