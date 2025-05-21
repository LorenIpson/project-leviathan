package game_forum_api.forum.pin.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.pin.service.ForumPinsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForumPinsController {

    private final ForumPinsService forumPinsService;

    private final MemberService memberService;

    public ForumPinsController(ForumPinsService forumPinsService, MemberService memberService) {
        this.forumPinsService = forumPinsService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/post/{postId}/addToForumPin")
    public ResponseEntity<String> addToForumPin(@MemberId Integer memberId,
                                                @PathVariable Long postId) {
        Member moderator = memberService.findById(memberId);
        String message = forumPinsService.addToForumPins(moderator, postId);
        return ResponseEntity.ok(message);
    }

    // ===== DELETE ========================================

    @DeleteMapping("/api/post/{postId}/removeFromForumPin")
    public ResponseEntity<String> removeFromForumPin(@MemberId Integer memberId,
                                                     @PathVariable Long postId) {
        Member moderator = memberService.findById(memberId);
        String message = forumPinsService.removeFromForumPins(moderator, postId);
        return ResponseEntity.ok(message);
    }
}
