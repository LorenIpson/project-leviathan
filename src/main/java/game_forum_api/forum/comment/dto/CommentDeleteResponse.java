package game_forum_api.forum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteResponse {

    private Long postId;
    private Long commentId;
    private Boolean isDeleted;
    private String message;

}
