package game_forum_api.forum.ban.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.ban.dto.ForumBanRequest;
import game_forum_api.forum.ban.dto.ForumBanResponse;
import game_forum_api.forum.ban.dto.ForumBanUpdateRequest;
import game_forum_api.forum.ban.dto.IsBannedResponse;
import game_forum_api.forum.ban.service.ForumsBansService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ForumBansController {

    private final ForumsBansService forumsBansService;

    private final MemberService memberService;

    public ForumBansController(ForumsBansService forumsBansService, MemberService memberService) {
        this.forumsBansService = forumsBansService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    /**
     * 將使用者加入 Forum 的懲罰名單。<br>
     * 需要該 Forum 的 Moderator 權限。
     */
    @PostMapping("/api/forum/{forumId}/moderator/addToForumBans")
    public ResponseEntity<ForumBanResponse> addToForumBans(@MemberId Integer memberId,
                                                           @PathVariable Integer forumId,
                                                           @RequestBody ForumBanRequest newBan) {
        Member moderator = memberService.findById(memberId);
        ForumBanResponse forumBanResponse = forumsBansService.addToForumBans(moderator, forumId, newBan);
        return ResponseEntity.ok(forumBanResponse);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得 Forum 的所有懲罰名單與細節。
     */
    @GetMapping("/api/forum/{forumId}/getAllForumBans")
    public ResponseEntity<List<ForumBanResponse>> getAllForumBans(@PathVariable Integer forumId) {
        List<ForumBanResponse> forumBans = forumsBansService.getForumBans(forumId);
        return ResponseEntity.ok(forumBans);
    }

    /**
     * 前端檢查懲罰狀態。<br>
     * 未登入情況下操作 @MemberId 會出現 Null Exception，所以會在 Console 出現錯誤警告。
     */
    @GetMapping("/api/forum/{forumId}/checkIsBanned")
    public ResponseEntity<IsBannedResponse> checkIsBanned(@MemberId Integer memberId,
                                                          @PathVariable Integer forumId) {
        Member member = memberService.findById(memberId);
        IsBannedResponse bannedInForum = forumsBansService.isBannedInForum(member, forumId);
        return ResponseEntity.ok(bannedInForum);
    }

    // ===== UPDATE ========================================

    /**
     * 更新使用者懲罰狀況。<br>
     * 會保留懲罰紀錄。
     */
    @PutMapping("/api/forum/{forumId}/moderator/updatePenalizedStatus")
    public ResponseEntity<String> updatePenalizedStatus(@MemberId Integer memberId,
                                                        @PathVariable Integer forumId,
                                                        @RequestBody ForumBanUpdateRequest banDTO) {
        Member moderator = memberService.findById(memberId);
        String message = forumsBansService.updatePenalizedStatus(moderator, forumId, banDTO);
        return ResponseEntity.ok(message);
    }

}
