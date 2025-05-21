package game_forum_api.forum.image.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.image.dto.CommentImageResponse;
import game_forum_api.forum.image.dto.CommentImageUploadResponse;
import game_forum_api.forum.image.service.CommentImageService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CommentImageController {

    private final CommentImageService commentImageService;

    private final MemberService memberService;

    public CommentImageController(CommentImageService commentImageService, MemberService memberService) {
        this.commentImageService = commentImageService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/comment/uploadImg")
    public ResponseEntity<CommentImageUploadResponse> uploadPostImages(@MemberId Integer memberId,
                                                                       @RequestParam("image") MultipartFile image) {
        Member member = memberService.findById(memberId);
        CommentImageUploadResponse imageUploadResponse = commentImageService.uploadCommentImage(image, member);
        return ResponseEntity.ok(imageUploadResponse);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有已發送的圖片。
     */
    @GetMapping("/api/image/comment/commentImages")
    public ResponseEntity<Page<CommentImageResponse>> findAllPostedImages(@MemberId Integer memberId,
                                                                          Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<CommentImageResponse> commentImages = commentImageService.findCommentImages(member, pageable);
        return ResponseEntity.ok(commentImages);
    }

    /**
     * 取得所有已上傳但未發送的圖片。
     */
    @GetMapping("/api/image/comment/tempImages")
    public ResponseEntity<Page<CommentImageResponse>> findAllTempImages(@MemberId Integer memberId,
                                                                        Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<CommentImageResponse> commentImages = commentImageService.findTempCommentImages(member, pageable);
        return ResponseEntity.ok(commentImages);
    }

    // ===== DELETE ========================================

    /**
     * 刪除使用者圖片。<br>
     * 永久！
     */
    @DeleteMapping("/api/image/comment/{imageId}/deleteCommentImage")
    public ResponseEntity<String> deleteCommentImage(@MemberId Integer memberId, @PathVariable Integer imageId) {
        Member member = memberService.findById(memberId);
        String message = commentImageService.deleteImage(member, imageId);
        return ResponseEntity.ok(message);
    }

}
