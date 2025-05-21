package game_forum_api.forum.view.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.view.service.PostsViewsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostsViewsController {

    private final PostsViewsService postsViewsService;

    private final MemberService memberService;

    public PostsViewsController(PostsViewsService postsViewsService, MemberService memberService) {
        this.postsViewsService = postsViewsService;
        this.memberService = memberService;
    }

    /**
     * 每點擊一次文章 Api，自動增加一筆觀看紀錄。
     */
    @PostMapping("/api/view/{postId}/increase")
    public ResponseEntity<Void> increaseViewsCount(@MemberId Integer memberId,
                                                   @PathVariable Long postId) {
        Member member = memberService.findById(memberId);
        postsViewsService.increaseViewsCount(member, postId);
        return ResponseEntity.ok().build();
    }

}
