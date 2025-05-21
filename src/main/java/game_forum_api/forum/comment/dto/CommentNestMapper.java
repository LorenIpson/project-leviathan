package game_forum_api.forum.comment.dto;

import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.util.TimeAgo;

import java.util.List;

public class CommentNestMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static CommentNestResponse toCommentNestResponse(Comments comment,
                                                            List<CommentNestResponse> replies,
                                                            Long voteCount) {
        if (!comment.getIsDeleted()) {
            return new CommentNestResponse(

                    comment.getId(),
                    comment.getMember().getId(),
                    comment.getMember().getUsername(),
                    comment.getMember().getPhoto(),

                    comment.getContent(),
                    comment.getCommentImages() != null ? comment.getCommentImages().getImageUrl() : null,

                    TimeAgo.toTimeAgo(comment.getCreatedAt()),
                    comment.getCreatedAt(),

                    comment.getIsEdited(),
                    TimeAgo.toTimeAgo(comment.getEditedAt()),
                    comment.getEditedAt(),

                    voteCount != null ? voteCount : 0,

                    comment.getIsLocked(),
                    comment.getLockedBy() != null ? comment.getLockedBy().getAccountId() : null,
                    comment.getIsDeleted(),

                    comment.getDeletedBy() != null ? comment.getDeletedBy().getAccountId() : null,

                    replies

            );
        } else {
            return new CommentNestResponse(

                    comment.getId(),
                    comment.getMember().getId(),
                    comment.getMember().getUsername(),
                    comment.getMember().getPhoto(),

                    "留言已被 " + comment.getDeletedBy().getUsername() + " 刪除。",
                    null,

                    TimeAgo.toTimeAgo(comment.getCreatedAt()),
                    comment.getCreatedAt(),

                    comment.getIsEdited(),
                    TimeAgo.toTimeAgo(comment.getEditedAt()),
                    comment.getEditedAt(),

                    voteCount != null ? voteCount : 0,

                    comment.getIsLocked(),
                    comment.getLockedBy() != null ? comment.getLockedBy().getAccountId() : null,
                    comment.getIsDeleted(),

                    comment.getDeletedBy() != null ? comment.getDeletedBy().getAccountId() : null,

                    replies

            );

        }
    }

}
