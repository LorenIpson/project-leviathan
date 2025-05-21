package game_forum_api.forum.favorite.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.favorite.dto.IsFavResponse;
import game_forum_api.forum.favorite.service.ForumFavService;
import game_forum_api.forum.forum.dto.ForumSummaryResponse;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForumFavController {

    private final ForumFavService forumFavService;

    private final MemberService memberService;

    public ForumFavController(ForumFavService forumFavService, MemberService memberService) {
        this.forumFavService = forumFavService;
        this.memberService = memberService;
    }

    // ===== RETRIEVE ========================================

    @GetMapping("/api/forum/allFav")
    public ResponseEntity<Page<ForumSummaryResponse>> findAllFavForumsByMember(@MemberId Integer memberId,
                                                                               Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<ForumSummaryResponse> allFav = forumFavService.findAllFavForumsByMember(member, pageable);
        return ResponseEntity.ok(allFav);
    }

    @GetMapping("/api/forum/{forumId}/isFav")
    public ResponseEntity<IsFavResponse> isFavForum(@MemberId Integer memberId,
                                                    @PathVariable Integer forumId) {
        Member member = memberService.findById(memberId);
        IsFavResponse isFav = forumFavService.isFav(member, forumId);
        return ResponseEntity.ok(isFav);
    }

    // ===== UPDATE ========================================

    @PutMapping("/api/forum/{forumId}/fav/addTo")
    public ResponseEntity<String> toggleAddToFav(@MemberId Integer memberId,
                                                 @PathVariable Integer forumId) {
        Member member = memberService.findById(memberId);
        String message = forumFavService.toggleForumFav(member, forumId);
        return ResponseEntity.ok(message);
    }

}
