package game_forum_api.forum.post.dto;

import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.tag.dto.PostTagsMapper;
import game_forum_api.forum.util.ExtractHtmlTags;
import game_forum_api.forum.util.ExtractImgurUrl;
import game_forum_api.forum.util.TimeAgo;

public class PostSummaryMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static PostSummaryResponse toPostSummaryResponseDTO(Posts post, Long voteCount, Long viewCount, Long commentCount) {

        if (!post.getIsDeleted()) {
            return new PostSummaryResponse(

                    post.getId(),
                    post.getMember().getUsername(),

                    PostTagsMapper.toPostTagsResponse(post.getPostTags()),
                    post.getTitle(),
                    ExtractHtmlTags.removeHtmlTags(post.getContent(), 50),
                    post.getSpoiler(),
                    ExtractImgurUrl.extractFirstImgurUrl(post.getContent()),

                    TimeAgo.toTimeAgo(post.getCreatedAt()),
                    post.getCreatedAt(),

                    post.getIsEdited(),
                    TimeAgo.toTimeAgo(post.getEditedAt()),
                    post.getEditedAt(),

                    post.getIsRecommended(),

                    post.getLatestCommentAt() != null ? TimeAgo.toTimeAgo(post.getLatestCommentAt()) : null,

                    voteCount != null ? voteCount : 0,
                    viewCount,
                    commentCount,

                    post.getIsLocked(),
                    post.getIsDeleted()

            );

        } else {
            return new PostSummaryResponse(

                    post.getId(),
                    post.getMember().getUsername(),

                    PostTagsMapper.toPostTagsResponse(post.getPostTags()),
                    "[ 文章已刪除 ] " + post.getTitle(),
                    "已被 " + post.getDeletedBy().getAccountId() + " 刪除。",
                    false,
                    null,

                    TimeAgo.toTimeAgo(post.getCreatedAt()),
                    post.getCreatedAt(),

                    post.getIsEdited(),
                    TimeAgo.toTimeAgo(post.getEditedAt()),
                    post.getEditedAt(),

                    post.getIsRecommended(),

                    post.getLatestCommentAt() != null ? TimeAgo.toTimeAgo(post.getLatestCommentAt()) : null,

                    voteCount != null ? voteCount : 0,
                    viewCount,
                    commentCount,

                    post.getIsLocked(),
                    post.getIsDeleted()

            );
        }
    }

}
