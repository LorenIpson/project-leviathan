package game_forum_api.forum.post.dto;

import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.tag.dto.PostTagsMapper;
import game_forum_api.forum.util.TimeAgo;

public class PostDetailsMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static PostDetailsResponse toPostResponseDTO(Posts post,
                                                        byte[] avatar,
                                                        Long voteCount,
                                                        Long viewCount,
                                                        Long commentCount) {

        if (!post.getIsDeleted()) {
            return new PostDetailsResponse(

                    post.getForum().getId(),
                    post.getId(),
                    post.getMember().getId(),
                    post.getMember().getAccountId(),
                    post.getMember().getUsername(),
                    avatar,

                    PostTagsMapper.toPostTagsResponse(post.getPostTags()),
                    post.getTitle(),
                    post.getContent(),
                    post.getSpoiler(),

                    TimeAgo.toTimeAgo(post.getCreatedAt()),
                    post.getCreatedAt(),

                    post.getIsEdited(),
                    TimeAgo.toTimeAgo(post.getEditedAt()),
                    post.getEditedAt(),

                    post.getIsRecommended(),

                    voteCount != null ? voteCount : 0,
                    viewCount,
                    commentCount,

                    post.getIsLocked(),
                    post.getLockedBy() != null ? post.getLockedBy().getAccountId() : null,
                    post.getIsDeleted(),
                    post.getDeletedBy() != null ? post.getDeletedBy().getAccountId() : null

            );
        } else {
            return new PostDetailsResponse(

                    post.getForum().getId(),
                    post.getId(),
                    post.getMember().getId(),
                    post.getMember().getAccountId(),
                    post.getMember().getUsername(),
                    avatar,

                    PostTagsMapper.toPostTagsResponse(post.getPostTags()),
                    "文章已刪除 " + post.getTitle(),
                    "已被 " + post.getDeletedBy().getUsername() + " 刪除。",
                    false,

                    TimeAgo.toTimeAgo(post.getCreatedAt()),
                    post.getCreatedAt(),

                    post.getIsEdited(),
                    TimeAgo.toTimeAgo(post.getEditedAt()),
                    post.getEditedAt(),

                    post.getIsRecommended(),

                    voteCount != null ? voteCount : 0,
                    viewCount,
                    commentCount,

                    post.getIsLocked(),
                    post.getLockedBy() != null ? post.getLockedBy().getAccountId() : null,
                    post.getIsDeleted(),
                    post.getDeletedBy() != null ? post.getDeletedBy().getAccountId() : null

            );
        }

    }

}
