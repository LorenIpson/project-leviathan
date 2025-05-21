package game_forum_api.forum.moderator.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.moderator.dto.ForumModRequest;
import game_forum_api.forum.moderator.dto.ForumModResponse;
import game_forum_api.forum.moderator.dto.IsModeratorResponse;
import game_forum_api.forum.moderator.service.ForumsModeratorsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ForumsModeratorsController {

    private final ForumsModeratorsService forumsModeratorsService;

    private final MemberService memberService;

    public ForumsModeratorsController(ForumsModeratorsService forumsModeratorsService, MemberService memberService) {
        this.forumsModeratorsService = forumsModeratorsService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    /**
     * 新增新的 Forum Moderator。<br>
     * 需要 Admin 或該 Forum 的 Moderator 權限。
     */
    @PostMapping("/api/forum/{forumId}/admin/addNewModerator")
    public ResponseEntity<String> addForumModerator(@MemberId Integer memberId,
                                                    @PathVariable Integer forumId,
                                                    @RequestBody ForumModRequest newModerator) {
        Member admin = memberService.findById(memberId);
        String message = forumsModeratorsService.addForumModerator(admin, forumId, newModerator);
        return ResponseEntity.ok(message);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得 Forum 所有的 Moderators。
     */
    @GetMapping("/api/forum/{forumId}/getModeratorsByForum")
    public ResponseEntity<ForumModResponse> getModeratorsByForum(@PathVariable Integer forumId) {
        ForumModResponse allForumModerators = forumsModeratorsService.getModeratorsByForum(forumId);
        return ResponseEntity.ok(allForumModerators);
    }

    /**
     * 檢查是否為該 Forum 的 Mod。
     */
    @GetMapping("/api/forum/{forumId}/isModerator")
    public ResponseEntity<IsModeratorResponse> isModerator(@MemberId Integer memberId,
                                                           @PathVariable Integer forumId) {
        if (memberId == null) {
            return ResponseEntity.ok(new IsModeratorResponse(false));
        }
        Member member = memberService.findById(memberId);
        IsModeratorResponse isModerator = forumsModeratorsService.isForumModerator(member, forumId);
        return ResponseEntity.ok(isModerator);
    }

    // ===== DELETE ========================================

    /**
     * 移除 Forum 的 Moderator。<br>
     * 需要 Admin 或該 Forum 的 Moderator 權限。
     */
    @PutMapping("/api/forum/{forumId}/admin/removeModerator")
    public ResponseEntity<String> removeForumModerator(@MemberId Integer memberId,
                                                       @PathVariable Integer forumId,
                                                       @RequestBody ForumModRequest moderator) {
        Member admin = memberService.findById(memberId);
        System.out.println("================================= 我在這裡 ==========================================");
        String message = forumsModeratorsService.deleteForumModerator(admin, forumId, moderator);
        return ResponseEntity.ok(message);
    }

}
