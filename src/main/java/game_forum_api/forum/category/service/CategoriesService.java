package game_forum_api.forum.category.service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.forum.category.dto.CategoryRequest;
import game_forum_api.forum.category.dto.CategoryResponse;
import game_forum_api.forum.category.dto.CategoryMapper;
import game_forum_api.forum.category.model.Categories;
import game_forum_api.forum.category.repository.CategoriesRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriesService {

    private final CategoriesRepository categoriesRepos;

    public CategoriesService(CategoriesRepository categoriesRepos) {
        this.categoriesRepos = categoriesRepos;
    }

    // ===== CREATE ========================================

    public CategoryResponse createNewCategory(Member admin, CategoryRequest category) {

        boolean isAdmin = admin.getRole() == 3;
        if (!isAdmin) {
            throw new UnauthorizedException("使用者沒有操作權限。");
        }

        Categories newCategory = new Categories();
        newCategory.setName(category.getName());
        newCategory.setColor(category.getColor());
        categoriesRepos.save(newCategory);
        return CategoryMapper.toCategoryResponseDTO(newCategory);

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有分類。<br>
     * 顯示在 ForumList 頁面。
     */
    public List<CategoryResponse> findAllCategories() {

        List<Categories> categories = categoriesRepos.findAll();
        return CategoryMapper.toCategoriesListResponseDTO(categories);

    }

    /**
     * 取得特定的分類。
     */
    public CategoryResponse getCategoryById(Integer id) {

        Optional<Categories> category = categoriesRepos.findById(id);
        if (category.isPresent()) {
            Categories targetCategory = category.get();
            return CategoryMapper.toCategoryResponseDTO(targetCategory);
        } else {
            throw new ResourceNotFoundException("找不到目標分類，ID： " + id);
        }

    }

}
