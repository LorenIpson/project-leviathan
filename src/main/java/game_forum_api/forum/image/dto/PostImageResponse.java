package game_forum_api.forum.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostImageResponse {

    private Integer imageId;
    private Long postId;
    private String imageUrl;
    private String deletehash;

}
