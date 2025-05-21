package game_forum_api.avatar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvatarPhotoResponse {
    private Integer id; // avatar_photo 表的 ID
    private String accountId; // 會員的 accountId
    private byte[] bodyPhoto; // 全身圖片路徑
    private byte[] facePhoto; // 頭像圖片路徑
}