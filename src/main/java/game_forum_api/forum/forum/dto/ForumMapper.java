package game_forum_api.forum.forum.dto;

import game_forum_api.forum.category.dto.CategoryMapper;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.util.ByteToBase64;

import java.util.List;
import java.util.stream.Collectors;

public class ForumMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static ForumResponse toForumResponseDTO(Forums forum) {
        return new ForumResponse(forum.getId(),
                forum.getName(),
                forum.getCover() != null ? ByteToBase64.toBase64(forum.getCover()) : null,
                forum.getPopularityScore(),
                CategoryMapper.toCategoriesListResponseDTO(forum.getCategories()));
    }

    // ===== List<ENTITY> TO List<RESPONSE> ========================================

    public static List<ForumResponse> toForumsListResponseDTO(List<Forums> forums) {
        return forums.stream()
                .map(forum -> new ForumResponse(
                        forum.getId(),
                        forum.getName(),
                        forum.getCover() != null ? ByteToBase64.toBase64(forum.getCover()) : null,
                        forum.getPopularityScore(),
                        CategoryMapper.toCategoriesListResponseDTO(forum.getCategories())
                )).collect(Collectors.toList());
    }

}
