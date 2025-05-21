package game_forum_api.avatar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavePhotoRequest {

    @NotNull(message = "全身圖片不能為空")
    private String bodyPhoto; // 全身圖片路徑

    @NotNull(message = "頭像圖片不能為空")
    private String facePhoto; // 頭像圖片路徑
}