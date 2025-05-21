package game_forum_api.forum.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentNestResponse {

    private Long commentId;
    private Integer memberId;
    private String username;
    private byte[] avatar;

    private String content;
    private String imageUrl;

    private String commentTimeAgo;
    private LocalDateTime createdAt;

    private Boolean isEdited;
    private String editTimeAgo;
    private LocalDateTime editedAt;

    private Long votes;

    private Boolean isLocked;
    private String lockedBy;

    private Boolean isDeleted;
    private String deletedBy;

    private List<CommentNestResponse> replies;

}
