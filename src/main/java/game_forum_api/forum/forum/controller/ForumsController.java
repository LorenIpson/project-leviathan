package game_forum_api.forum.forum.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.forum.dto.ForumCreateResponse;
import game_forum_api.forum.forum.dto.ForumRequest;
import game_forum_api.forum.forum.dto.ForumResponse;
import game_forum_api.forum.forum.dto.ForumSummaryResponse;
import game_forum_api.forum.forum.service.ForumsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
public class ForumsController {

    private final ForumsService forumsService;

    private final MemberService memberService;

    public ForumsController(ForumsService forumsService, MemberService memberService) {
        this.forumsService = forumsService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    /**
     * 新增新的 Forum。<br>
     * 需要 Admin 權限。
     */
    @PostMapping("/api/forum/admin/createNewForum")
    public ResponseEntity<ForumCreateResponse> createNewForum(@MemberId Integer memberId,
                                                              @RequestParam String name,
                                                              @RequestParam MultipartFile mainCover,
                                                              @RequestParam MultipartFile secondaryCover,
                                                              @RequestParam String description,
                                                              @RequestParam Set<Integer> categories) {
        Member admin = memberService.findById(memberId);
        ForumRequest newForumDTO = new ForumRequest(name, mainCover, secondaryCover, description, categories);
        ForumCreateResponse newForum = forumsService.createNewForums(admin, newForumDTO);
        return ResponseEntity.ok(newForum);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有的 Forum 到 Forum Summary。<br>
     * 依 Forum ID 排序。
     */
    @GetMapping("/api/forum/findAllForums")
    public ResponseEntity<List<ForumResponse>> findAllForums() {
        List<ForumResponse> allForums = forumsService.findAllForums();
        return ResponseEntity.ok(allForums);
    }

    /**
     * 取得所有的 Forum 到 Forum Summary。<br>
     * 依 Popularity 排序。
     */
    @GetMapping("/api/forum/findForumsByPopularity")
    public ResponseEntity<Page<ForumResponse>> findAllForumsSortByPopularity(@PageableDefault(sort = "popularityScore",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ForumResponse> sortByPopularity = forumsService.findAllForumsSortByPopularity(pageable);
        return ResponseEntity.ok(sortByPopularity);
    }

    /**
     * 使用 Category 篩選討論區。<br>
     * 其實只支援單一個條件過濾。
     */
    @GetMapping("/api/forum/findForumsByPopularityAndCategory")
    public ResponseEntity<Page<ForumSummaryResponse>> byPopularityAndTag(@RequestParam(required = false) String name,
                                                                         @RequestParam(required = false)
                                                                         List<Integer> categoryId,
                                                                         @PageableDefault(sort = "popularityScore",
                                                                                 direction = Sort.Direction.DESC)
                                                                         Pageable pageable) {
        Page<ForumSummaryResponse> sortByPopularityAndTags = forumsService
                .findForumsByPopularityAndTag(name, categoryId, pageable);
        return ResponseEntity.ok(sortByPopularityAndTags);
    }

    // ===== UPDATE ========================================

    /**
     * 更新討論板封面。<br>
     * 需要 Admin 或該 Forum 的 Moderator 權限。
     */
    @PutMapping("/api/forum/{forumId}/admin/updateCover")
    public ResponseEntity<?> editForumCover(@MemberId Integer memberId,
                                            @PathVariable Integer forumId,
                                            @RequestParam MultipartFile cover) {
        Member admin = memberService.findById(memberId);
        ForumRequest coverDTO = new ForumRequest(cover);
        String message = forumsService.editForumCover(admin, forumId, coverDTO);
        return ResponseEntity.ok(message);
    }

}