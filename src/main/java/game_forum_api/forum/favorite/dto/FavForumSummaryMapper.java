package game_forum_api.forum.favorite.dto;

import game_forum_api.forum.category.dto.CategoryMapper;
import game_forum_api.forum.favorite.model.ForumFav;
import game_forum_api.forum.forum.dto.ForumSummaryResponse;
import game_forum_api.forum.util.ByteToBase64;

public class FavForumSummaryMapper {

    public static ForumSummaryResponse toFavForumSummaryResponse(ForumFav forumFav,
                                                                 Long topPostId,
                                                                 String topPostTitle) {
        return new ForumSummaryResponse(
                forumFav.getForum().getId(),
                forumFav.getForum().getName(),
                forumFav.getForum().getCover() != null ? ByteToBase64.toBase64(forumFav.getForum().getCover()) : null,
                forumFav.getForum().getPopularityScore(),
                CategoryMapper.toCategoriesListResponseDTO(forumFav.getForum().getCategories()),
                topPostId,
                topPostTitle
        );
    }

}
