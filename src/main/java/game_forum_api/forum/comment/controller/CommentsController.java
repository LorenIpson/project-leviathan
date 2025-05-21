package game_forum_api.forum.comment.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.comment.dto.CommentDeleteResponse;
import game_forum_api.forum.comment.dto.CommentNestResponse;
import game_forum_api.forum.comment.dto.CommentRequest;
import game_forum_api.forum.comment.dto.CommentResponse;
import game_forum_api.forum.comment.service.CommentsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentsController {

    private final CommentsService commentsService;

    private final MemberService memberService;

    public CommentsController(CommentsService commentsService, MemberService memberService) {
        this.commentsService = commentsService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    /**
     * 新增一筆父級留言。
     */
    @PostMapping("/api/comment/{postId}/createNewComment")
    public ResponseEntity<CommentResponse> createCommentByPostId(@MemberId Integer memberId,
                                                                 @PathVariable Long postId,
                                                                 @RequestBody CommentRequest commentDTO) {
        Member member = memberService.findById(memberId);
        CommentResponse newParentComment = commentsService.createNewCommentByPostId(member, postId, commentDTO);
        return ResponseEntity.ok(newParentComment);
    }

    /**
     * 新增一筆子級留言。
     */
    @PostMapping("/api/comment/{postId}/{parentCommentId}/createNewComment")
    public ResponseEntity<CommentResponse> createCommentByParentCommentId(@MemberId Integer memberId,
                                                                          @PathVariable Long postId,
                                                                          @PathVariable Long parentCommentId,
                                                                          @RequestBody CommentRequest commentDTO) {
        Member member = memberService.findById(memberId);
        CommentResponse newChildComment = commentsService
                .createNewCommentByParentCommentId(member, postId, parentCommentId, commentDTO);
        return ResponseEntity.ok(newChildComment);
    }

    // ===== RETRIEVE ========================================

    /**
     * 查詢所有留言，以樹狀結構顯示。
     */
    @GetMapping("/api/comment/{postId}/allComments")
    public ResponseEntity<List<CommentNestResponse>> getAllCommentsByPostId(@PathVariable Long postId) {
        List<CommentNestResponse> allComments = commentsService.getCommentsByPostId(postId);
        return ResponseEntity.ok(allComments);
    }

    /**
     * 父級留言搜尋功能。
     */
    @GetMapping("/api/comment/{postId}/commentsBySearch")
    public ResponseEntity<List<CommentNestResponse>> getCommentsBySearch(@PathVariable Long postId,
                                                                         @RequestParam String keyword) {
        List<CommentNestResponse> allComments = commentsService.getCommentsBySearch(postId, keyword);
        return ResponseEntity.ok(allComments);
    }

    // ===== UPDATE ========================================

    /**
     * 發文者自行更新留言。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    @PutMapping("/api/{commentId}/editComment")
    public ResponseEntity<String> editCommentByCommentId(@MemberId Integer memberId,
                                                         @PathVariable Long commentId,
                                                         @RequestBody CommentRequest comment) {
        Member member = memberService.findById(memberId);
        String message = commentsService.editCommentByCommentId(member, commentId, comment);
        return ResponseEntity.ok(message);
    }

    // ===== DELETE ========================================

    /**
     * Moderator 鎖定並且刪除留言。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    @DeleteMapping("/api/moderator/{commentId}/toggleCommentIsDeleted")
    public ResponseEntity<?> toggleCommentIsDeletedByCommentId(@MemberId Integer memberId,
                                                               @PathVariable Long commentId) {
        Member moderator = memberService.findById(memberId);
        String message = commentsService.toggleDeletedByCommentId(moderator, commentId);
        return ResponseEntity.ok(message);
    }

    /**
     * Member 鎖定並且刪除留言。<br>
     * 不可逆操作。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    @DeleteMapping("/api/comment/{commentId}/author/deleteCommentByCommentId")
    public ResponseEntity<CommentDeleteResponse> deleteCommentByCommentId(@MemberId Integer memberId,
                                                                          @PathVariable Long commentId) {
        Member member = memberService.findById(memberId);
        CommentDeleteResponse commentDeleteResponse = commentsService
                .deleteCommentByCommentIdAndMember(member, commentId);
        return ResponseEntity.ok(commentDeleteResponse);
    }

}
