package game_forum_api.forum.comment.dto;

import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.util.TimeAgo;

public class CommentMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static CommentResponse toCommentResponse(Comments comment) {
        return new CommentResponse(

                comment.getPost().getId(),
                comment.getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,

                comment.getMember().getId(),
                comment.getMember().getAccountId(),
                comment.getMember().getUsername(),
                comment.getMember().getPhoto(),

                comment.getContent(),
                comment.getCommentImages() != null ? comment.getCommentImages().getImageUrl() : null,

                TimeAgo.toTimeAgo(comment.getCreatedAt()),
                comment.getCreatedAt(),

                comment.getIsEdited(),
                TimeAgo.toTimeAgo(comment.getEditedAt()),
                comment.getEditedAt(),

                comment.getIsLocked(),
                comment.getLockedBy() != null ? comment.getLockedBy().getAccountId() : null,

                comment.getIsDeleted(),
                comment.getDeletedBy() != null ? comment.getDeletedBy().getAccountId() : null

        );
    }

}
