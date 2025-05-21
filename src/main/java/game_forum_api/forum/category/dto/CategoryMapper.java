package game_forum_api.forum.category.dto;

import game_forum_api.forum.category.model.Categories;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static CategoryResponse toCategoryResponseDTO(Categories category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getColor());
    }

    // ===== List<ENTITY> TO List<RESPONSE> ========================================

    public static List<CategoryResponse> toCategoriesListResponseDTO(List<Categories> categories) {
        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getColor()
                )).collect(Collectors.toList());
    }

}
