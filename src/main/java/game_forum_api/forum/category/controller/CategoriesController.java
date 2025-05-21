package game_forum_api.forum.category.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.category.dto.CategoryRequest;
import game_forum_api.forum.category.dto.CategoryResponse;
import game_forum_api.forum.category.service.CategoriesService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriesController {

    private final CategoriesService categoriesService;

    private final MemberService memberService;

    public CategoriesController(CategoriesService categoriesService, MemberService memberService) {
        this.categoriesService = categoriesService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/category/admin/createNewCategory")
    public ResponseEntity<CategoryResponse> createNewCategories(@MemberId Integer memberId,
                                                                @RequestBody CategoryRequest category) {
        Member admin = memberService.findById(memberId);
        CategoryResponse newCategory = categoriesService.createNewCategory(admin, category);
        return ResponseEntity.ok(newCategory);
    }

    // ===== RETRIEVE ========================================

    @GetMapping("/api/category/findAllCategories")
    public ResponseEntity<List<CategoryResponse>> findAllCategories() {
        List<CategoryResponse> allCategories = categoriesService.findAllCategories();
        return ResponseEntity.ok(allCategories);
    }

    @GetMapping("/api/category/{categoryId}/getCategory")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Integer categoryId) {
        CategoryResponse targetCategory = categoriesService.getCategoryById(categoryId);
        return ResponseEntity.ok(targetCategory);
    }

}
