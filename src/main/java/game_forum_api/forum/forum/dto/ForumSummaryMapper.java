package game_forum_api.forum.forum.dto;

import game_forum_api.forum.category.dto.CategoryMapper;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.util.ByteToBase64;

public class ForumSummaryMapper {

    public static ForumSummaryResponse toForumSummaryResponseDTO(Forums forum, Long topPostId, String topPostTitle) {
        return new ForumSummaryResponse(
                forum.getId(),
                forum.getName(),
                forum.getCover() != null ? ByteToBase64.toBase64(forum.getCover()) : null,
                forum.getPopularityScore(),
                CategoryMapper.toCategoriesListResponseDTO(forum.getCategories()),
                topPostId,
                topPostTitle
        );
    }

}
