package game_forum_api.forum.image.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.image.dto.PostImageResponse;
import game_forum_api.forum.image.dto.PostImageUploadResponse;
import game_forum_api.forum.image.service.PostImagesService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PostImagesController {

    private final PostImagesService postImagesService;

    private final MemberService memberService;

    public PostImagesController(PostImagesService postImagesService, MemberService memberService) {
        this.postImagesService = postImagesService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/post/uploadImg")
    public ResponseEntity<PostImageUploadResponse> uploadPostImages(@MemberId Integer memberId,
                                                                    @RequestParam("image") MultipartFile image) {
        Member member = memberService.findById(memberId);
        PostImageUploadResponse postImageUploadResponse = postImagesService.uploadPostImages(image, member);
        return ResponseEntity.ok(postImageUploadResponse);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有已發送的圖片。
     */
    @GetMapping("/api/image/post/postedImages")
    public ResponseEntity<Page<PostImageResponse>> findAllPostedImages(@MemberId Integer memberId, Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<PostImageResponse> postedPostImages = postImagesService.findPostedPostImages(member, pageable);
        return ResponseEntity.ok(postedPostImages);
    }

    /**
     * 取得所有已上傳但未發送的圖片。
     */
    @GetMapping("/api/image/post/tempImages")
    public ResponseEntity<Page<PostImageResponse>> findAllTempImages(@MemberId Integer memberId, Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<PostImageResponse> tempPostImages = postImagesService.findTempPostImages(member, pageable);
        return ResponseEntity.ok(tempPostImages);
    }

    // ===== DELETE ========================================

    /**
     * 刪除使用者圖片。<br>
     * 永久！
     */
    @DeleteMapping("/api/image/post/{imageId}/deletePostImage")
    public ResponseEntity<String> deletePostImage(@MemberId Integer memberId, @PathVariable Integer imageId) {
        Member member = memberService.findById(memberId);
        String message = postImagesService.deleteImage(member, imageId);
        return ResponseEntity.ok(message);
    }

}
