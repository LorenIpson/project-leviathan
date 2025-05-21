package game_forum_api.forum.image.service;

import com.fasterxml.jackson.databind.JsonNode;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.image.dto.CommentImageResponse;
import game_forum_api.forum.image.dto.CommentImageUploadResponse;
import game_forum_api.forum.image.dto.ImageMapper;
import game_forum_api.forum.image.model.CommentImages;
import game_forum_api.forum.image.repository.CommentImageRepository;
import game_forum_api.forum.util.ByteToBase64;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class CommentImageService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String IMGUR_UPLOAD_API = "https://api.imgur.com/3/image";
    private static final String IMGUR_DELETEHASH_API = "https://api.imgur.com/3/image/";
    private static final String CLIENT_ID = "36517971bea2f6a";

    private final CommentImageRepository commentImageRepos;

    private final MemberRepository memberRepos;

    public CommentImageService(CommentImageRepository commentImageRepos, MemberRepository memberRepos) {
        this.commentImageRepos = commentImageRepos;
        this.memberRepos = memberRepos;
    }

    // ===== CREATE ========================================

    public CommentImageUploadResponse uploadCommentImage(MultipartFile image, Member member) {

        Member targetMember = memberRepos.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("Member Not Found"));

        try {
            String toBase64 = ByteToBase64.toBase64(image.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Client-ID " + CLIENT_ID);

            Map<String, String> body = new HashMap<>();
            body.put("image", toBase64);
            body.put("type", "base64");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    IMGUR_UPLOAD_API,
                    HttpMethod.POST,
                    request,
                    JsonNode.class
            );

            JsonNode data = Objects.requireNonNull(response.getBody()).get("data");
            String imageUrl = data.get("link").asText();
            String deleteHash = data.get("deletehash").asText();

            CommentImages newImage = new CommentImages();
            newImage.setImageUrl(imageUrl);
            newImage.setDeleteHash(deleteHash);
            newImage.setUploadedAt(LocalDateTime.now());
            newImage.setMember(targetMember);
            newImage.setIsTemp(true);
            commentImageRepos.save(newImage);

            return new CommentImageUploadResponse(newImage.getId(), imageUrl);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有已發送的圖片。
     */
    public Page<CommentImageResponse> findCommentImages(Member member, Pageable pageable) {

        Page<CommentImages> byMemberAndIsTemp = commentImageRepos
                .findByMemberAndIsTemp(member, false, pageable);
        return byMemberAndIsTemp.map(ImageMapper::toCommentImageResponse);

    }

    /**
     * 取得所有已上傳但未發送的圖片。
     */
    public Page<CommentImageResponse> findTempCommentImages(Member member, Pageable pageable) {

        Page<CommentImages> byMemberAndIsTemp = commentImageRepos
                .findByMemberAndIsTemp(member, true, pageable);
        return byMemberAndIsTemp.map(ImageMapper::toCommentImageResponse);

    }

    // ===== DELETE ========================================

    /**
     * 刪除使用者圖片。<br>
     * 永久！
     */
    public String deleteImage(Member member, Integer imageId) {

        CommentImages targetImage = commentImageRepos.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標圖片。"));

        if (!targetImage.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("沒有操作權限。");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Client-ID " + CLIENT_ID);

        try {
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    IMGUR_DELETEHASH_API + targetImage.getDeleteHash(),
                    HttpMethod.DELETE,
                    request,
                    JsonNode.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                return "刪除失敗。";
            }

            commentImageRepos.delete(targetImage);
            return "刪除成功！";
        } catch (RestClientException e) {
            throw new RuntimeException("操作失敗" + e);
        }

    }

}
