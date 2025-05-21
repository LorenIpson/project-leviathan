package game_forum_api.forum.vote.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.vote.dto.PostsVotesRequest;
import game_forum_api.forum.vote.dto.PostsVotesResponse;
import game_forum_api.forum.vote.service.PostsVotesService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostVotesController {

    private final PostsVotesService postsVotesService;

    private final MemberService memberService;

    public PostVotesController(PostsVotesService postsVotesService, MemberService memberService) {
        this.postsVotesService = postsVotesService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @Deprecated
    @PostMapping("/api/post/{postId}/vote/deprecated")
    public ResponseEntity<?> voteByPostId(@MemberId Integer memberId,
                                          @PathVariable Long postId,
                                          @RequestBody PostsVotesRequest voteDTO) {
        Member member = memberService.findById(memberId);
        String message = postsVotesService.postVote(member, postId, voteDTO);
        return ResponseEntity.ok(message);
    }

    // ===== RETRIEVE ========================================

    @GetMapping("/api/post/{postId}/getMemberVote")
    public ResponseEntity<PostsVotesResponse> getMemberPostVote(@MemberId Integer memberId,
                                                                @PathVariable Long postId) {
        Member member = memberService.findById(memberId);
        PostsVotesResponse memberPostVote = postsVotesService.getMemberPostVote(member, postId);
        return ResponseEntity.ok(memberPostVote);
    }

    // ===== UPDATE ========================================

    @PutMapping("/api/post/{postId}/vote")
    public ResponseEntity<PostsVotesResponse> operatePostVote(@MemberId Integer memberId,
                                                              @PathVariable Long postId,
                                                              @RequestBody PostsVotesRequest voteDTO) {
        Member member = memberService.findById(memberId);
        PostsVotesResponse postsVotesResponse = postsVotesService.operatePostVote(member, postId, voteDTO);
        return ResponseEntity.ok(postsVotesResponse);
    }

}
