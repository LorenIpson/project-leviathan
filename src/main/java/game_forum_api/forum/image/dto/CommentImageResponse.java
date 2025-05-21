package game_forum_api.forum.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentImageResponse {

    private Integer imageId;
    private Long postId; // 懶ㄉ寫跳轉到 comment 的 API 和前端功能qq
    private String imageUrl;
    private String deletehash;

}
