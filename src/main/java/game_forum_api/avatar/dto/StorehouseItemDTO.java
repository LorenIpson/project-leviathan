package game_forum_api.avatar.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorehouseItemDTO {
    private Integer id;          
    private Integer commodityId;
    private String commodityName;
    private String type;
    private String photoPath;
    private Date shelfTime;
    private Integer point;
    private Integer equipmentStatus;


    public StorehouseItemDTO(Integer id, String commodityName, String type, 
                           String photoPath, Date shelfTime, 
                           Integer point, Integer equipmentStatus) {
        this.id = id;
        this.commodityName = commodityName;
        this.type = type;
        this.photoPath = photoPath;
        this.shelfTime = shelfTime;
        this.point = point;
        this.equipmentStatus = equipmentStatus;
    }

    public StorehouseItemDTO(Integer id, Integer commodityId, String commodityName,
                           String type, String photoPath, Date shelfTime,
                           Integer point, Integer equipmentStatus) {
        this.id = id;
        this.commodityId = commodityId;
        this.commodityName = commodityName;
        this.type = type;
        this.photoPath = photoPath;
        this.shelfTime = shelfTime;
        this.point = point;
        this.equipmentStatus = equipmentStatus;
    }
}