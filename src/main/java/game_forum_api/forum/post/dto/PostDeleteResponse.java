package game_forum_api.forum.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDeleteResponse {

    private Long postId;
    private Boolean isDeleted;
    private String message;

}
