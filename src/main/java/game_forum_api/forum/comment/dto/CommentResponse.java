package game_forum_api.forum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long postId;
    private Long commentId;
    private Long parentCommentId;

    private Integer memberId;
    private String accountId;
    private String username;
    private byte[] avatar;

    private String content;
    private String imageUrl;

    private String commentTimeAgo;
    private LocalDateTime createdAt;

    private Boolean isEdited;
    private String editTimeAgo;
    private LocalDateTime editedAt;

    private Boolean isLocked;
    private String lockedBy;

    private Boolean isDeleted;
    private String deletedBy;

}
