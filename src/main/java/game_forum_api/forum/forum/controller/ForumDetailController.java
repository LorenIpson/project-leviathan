package game_forum_api.forum.forum.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.forum.dto.ForumDetailRequest;
import game_forum_api.forum.forum.dto.ForumDetailResponse;
import game_forum_api.forum.forum.service.ForumDetailService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ForumDetailController {

    private final ForumDetailService forumDetailService;

    private final MemberService memberService;

    public ForumDetailController(ForumDetailService forumDetailService, MemberService memberService) {
        this.forumDetailService = forumDetailService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/forum/{forumId}/detail/moderator/create")
    public ResponseEntity<ForumDetailResponse> createNewForumDetail(@MemberId Integer memberId,
                                                                    @PathVariable Integer forumId,
                                                                    @RequestParam MultipartFile cover,
                                                                    @RequestParam String description) {
        Member admin = memberService.findById(memberId);
        ForumDetailRequest newDetailRequest = new ForumDetailRequest(cover, description);
        ForumDetailResponse newDetailResponse = forumDetailService
                .createForumDetail(admin, newDetailRequest, forumId);
        return ResponseEntity.ok(newDetailResponse);
    }

    // ===== RETRIEVE ========================================

    @GetMapping("/api/forum/{forumId}/detail")
    public ResponseEntity<ForumDetailResponse> getForumDetail(@PathVariable Integer forumId) {
        ForumDetailResponse forumDetail = forumDetailService.getForumDetail(forumId);
        return ResponseEntity.ok(forumDetail);
    }

    // ===== UPDATE ========================================

    @PutMapping("/api/forum/{forumId}/detail/moderator/update")
    public ResponseEntity<String> updateDetail(@MemberId Integer memberId,
                                               @PathVariable Integer forumId,
                                               @RequestParam(required = false) MultipartFile secondaryCover,
                                               @RequestParam String description) {
        Member moderator = memberService.findById(memberId);
        String message = forumDetailService.updateForumDetail(moderator, forumId, secondaryCover, description);
        return ResponseEntity.ok(message);
    }

}
