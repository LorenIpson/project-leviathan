package game_forum_api.forum.vote.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.vote.dto.CommentsVotesRequest;
import game_forum_api.forum.vote.dto.CommentsVotesResponse;
import game_forum_api.forum.vote.service.CommentsVotesService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentVoteController {

    private final CommentsVotesService commentsVotesService;

    private final MemberService memberService;

    public CommentVoteController(CommentsVotesService commentsVotesService, MemberService memberService) {
        this.commentsVotesService = commentsVotesService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @Deprecated
    @PostMapping("/api/comment/{commentId}/vote/deprecated")
    public ResponseEntity<?> voteByCommentId(@MemberId Integer memberId,
                                             @PathVariable Long commentId,
                                             @RequestBody CommentsVotesRequest votesDTO) {
        Member member = memberService.findById(memberId);
        String message = commentsVotesService.commentVote(member, commentId, votesDTO);
        return ResponseEntity.ok(message);
    }

    // ===== RETRIEVE ========================================

    @GetMapping("/api/{postId}/comment/getMemberVote")
    public ResponseEntity<List<CommentsVotesResponse>> getMemberVote(@MemberId Integer memberId,
                                                                     @PathVariable Long postId) {
        Member member = memberService.findById(memberId);
        List<CommentsVotesResponse> memberCommentsVotes = commentsVotesService.getMemberCommentVote(member, postId);
        return ResponseEntity.ok(memberCommentsVotes);
    }

    // ===== UPDATE ========================================

    @PutMapping("/api/comment/{commentId}/vote")
    public ResponseEntity<CommentsVotesResponse> operateCommentVote(@MemberId Integer memberId,
                                                                    @PathVariable Long commentId,
                                                                    @RequestBody CommentsVotesRequest voteDTO) {
        Member member = memberService.findById(memberId);
        CommentsVotesResponse commentsVotesResponse = commentsVotesService
                .operateCommentVote(member, commentId, voteDTO);
        return ResponseEntity.ok(commentsVotesResponse);
    }

}
