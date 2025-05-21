package game_forum_api.forum.tag.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.tag.dto.*;
import game_forum_api.forum.tag.service.ForumTagsService;
import game_forum_api.forum.tag.service.TagRecommendService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ForumTagsController {

    private final ForumTagsService forumTagsService;

    private final TagRecommendService tagRecommendService;

    private final MemberService memberService;

    public ForumTagsController(ForumTagsService forumTagsService,
                               TagRecommendService tagRecommendService,
                               MemberService memberService) {
        this.forumTagsService = forumTagsService;
        this.tagRecommendService = tagRecommendService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/{forumId}/tag/moderator/createTagByForum")
    public ResponseEntity<ForumTagsResponse> createNewTag(@MemberId Integer memberId,
                                                          @PathVariable Integer forumId,
                                                          @RequestBody ForumTagsRequest forumTag) {
        Member moderator = memberService.findById(memberId);
        ForumTagsResponse newTag = forumTagsService.createForumTag(moderator, forumId, forumTag);
        return ResponseEntity.ok(newTag);
    }

    /**
     * 讀取使用者 onBlur 時傳送的文章標題。<br>
     * 使用 OpenAI API 處理分析並使用 Forum 所有可用 Tags 做分析推薦。
     */
    @PostMapping("/api/{forumId}/tag/recommend")
    public ResponseEntity<String> recommendTag(@PathVariable Integer forumId,
                                               @RequestBody RecommendPromptRequest titleDTO) {
        String tags = tagRecommendService.tagRecommend(forumId, titleDTO.getTitle());
        return ResponseEntity.ok(tags);
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有標籤。<br>
     * 包含 is_active = false。<br>
     * 給版主在設定頁面與發文時顯示。
     */
    @GetMapping("/api/{forumId}/tag/allTags")
    public ResponseEntity<List<ForumTagsResponse>> getAllTagsByForum(@PathVariable Integer forumId) {
        List<ForumTagsResponse> allTags = forumTagsService.getAllForumTags(forumId);
        return ResponseEntity.ok(allTags);
    }

    /**
     * 取得 is_active 標籤。<br>
     * 發文頁面與文章列表上方顯示用。
     */
    @GetMapping("/api/{forumId}/tag/activeTags")
    public ResponseEntity<List<ForumTagsResponse>> getActiveTagsByForum(@PathVariable Integer forumId) {
        List<ForumTagsResponse> activeForumTags = forumTagsService.getActiveForumTags(forumId);
        return ResponseEntity.ok(activeForumTags);
    }

    /**
     * 取得 is_active 標籤。<br>
     * 發文頁面與文章列表上方顯示用。
     */
    @GetMapping("/api/tag/{tagId}/getTagByTagId")
    public ResponseEntity<ForumTagsResponse> getTagByTagId(@PathVariable Long tagId) {
        ForumTagsResponse targetTag = forumTagsService.getTagByTagId(tagId);
        return ResponseEntity.ok(targetTag);
    }
    
    // ===== UPDATE ========================================

    /**
     * 停用討論區標籤。
     */
    @PutMapping("/api/tag/moderator/disableTagByTagId")
    public ResponseEntity<String> disableTagByTagId(@MemberId Integer memberId,
                                                    @RequestParam Long tagId) {
        Member moderator = memberService.findById(memberId);
        String message = forumTagsService.disableForumTag(moderator, tagId);
        return ResponseEntity.ok(message);
    }

}
