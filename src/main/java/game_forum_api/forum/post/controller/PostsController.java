package game_forum_api.forum.post.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.forum.post.dto.*;
import game_forum_api.forum.post.service.PostsService;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostsController {

    private final PostsService postsService;

    private final MemberService memberService;

    public PostsController(PostsService postsService, MemberService memberService) {
        this.postsService = postsService;
        this.memberService = memberService;
    }

    // ===== CREATE ========================================

    @PostMapping("/api/{forumId}/post/create")
    public ResponseEntity<Long> createNewPostByForum(@MemberId Integer memberId,
                                                     @PathVariable Integer forumId,
                                                     @RequestBody PostCreateRequest post) {
        Member member = memberService.findById(memberId);
        Long newPostID = postsService.createNewPostByForumId(member, forumId, post);
        return ResponseEntity.ok(newPostID);
    }

    // ===== RETRIEVE ===== ALL POSTS ========================================

    /**
     * 討論區文章列表。<br>
     * 使用 createdAt 排序。
     */
    @GetMapping("/api/{forumId}/posts/createdAt")
    public ResponseEntity<Page<PostSummaryResponse>> sortByCreatedAt(@PathVariable Integer forumId,
                                                                     @RequestParam(required = false) List<Long> tagId,
                                                                     @PageableDefault(
                                                                             sort = "createdAt",
                                                                             direction = Sort.Direction.DESC
                                                                     ) Pageable pageable) {
        Page<PostSummaryResponse> byCreatedAt = postsService.sortByCreatedAt(forumId, tagId, pageable);
        return ResponseEntity.ok(byCreatedAt);
    }

    /**
     * 討論區文章列表。<br>
     * 使用 latestComment 排序。
     */
    @GetMapping("/api/{forumId}/posts/latestComment")
    public ResponseEntity<Page<PostSummaryResponse>> sortByLatestComment(@PathVariable Integer forumId,
                                                                         @RequestParam(required = false) List<Long> tagId,
                                                                         @PageableDefault(
                                                                                 sort = "latestCommentAt",
                                                                                 direction = Sort.Direction.DESC
                                                                         ) Pageable pageable) {
        Page<PostSummaryResponse> byLatestComment = postsService.sortByLatestComment(forumId, tagId, pageable);
        return ResponseEntity.ok(byLatestComment);
    }

    /**
     * 討論區文章列表。<br>
     * 使用 voteScore 排序。
     */
    @GetMapping("/api/{forumId}/posts/popularity")
    public ResponseEntity<Page<PostSummaryResponse>> sortByScore(@PathVariable Integer forumId,
                                                                 @RequestParam(required = false) List<Long> tagId,
                                                                 @PageableDefault(
                                                                         sort = "popularityScore",
                                                                         direction = Sort.Direction.DESC
                                                                 ) Pageable pageable) {
        Page<PostSummaryResponse> byScoreAndTag = postsService.sortByVoteScore(forumId, tagId, pageable);
        return ResponseEntity.ok(byScoreAndTag);
    }

    // ===== RETRIEVE ===== PINNED POSTS ========================================

    /**
     * 討論區置頂文章。
     */
    @GetMapping("/api/{forumId}/posts/pinnedPosts")
    public ResponseEntity<List<PostSummaryResponse>> pinnedPosts(@PathVariable Integer forumId) {
        List<PostSummaryResponse> pinnedPosts = postsService.findPinnedPosts(forumId);
        return ResponseEntity.ok(pinnedPosts);
    }

    // ===== RETRIEVE ===== POSTS BY SEARCH RESULTS ========================================

    /**
     * 搜尋標題。<br>
     * 依照 Popularity 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/title/popularity")
    public ResponseEntity<Page<PostSummaryResponse>> searchByTitleSortByPop(@PathVariable Integer forumId,
                                                                            @RequestParam(required = false)
                                                                            String title,
                                                                            @PageableDefault(
                                                                                    sort = "popularityScore",
                                                                                    direction = Sort.Direction.DESC
                                                                            ) Pageable pageable) {
        Page<PostSummaryResponse> byPopularityAndParam = postsService.searchByTitle(forumId, title, pageable);
        return ResponseEntity.ok(byPopularityAndParam);
    }

    /**
     * 搜尋標題。<br>
     * 依照 latestCommentAt 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/title/latestComment")
    public ResponseEntity<Page<PostSummaryResponse>> searchByTitleSortByLatestC(@PathVariable Integer forumId,
                                                                                @RequestParam String title,
                                                                                @PageableDefault(
                                                                                        sort = "latestCommentAt",
                                                                                        direction = Sort.Direction.DESC
                                                                                ) Pageable pageable) {
        Page<PostSummaryResponse> byLatestComment = postsService.searchByTitle(forumId, title, pageable);
        return ResponseEntity.ok(byLatestComment);
    }

    /**
     * 搜尋標題。<br>
     * 依照 createdAt 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/title/createdAt")
    public ResponseEntity<Page<PostSummaryResponse>> searchByTitleSortByLatestPost(@PathVariable Integer forumId,
                                                                                   @RequestParam String title,
                                                                                   @PageableDefault(
                                                                                           sort = "createdAt",
                                                                                           direction = Sort
                                                                                                   .Direction
                                                                                                   .DESC
                                                                                   ) Pageable pageable) {
        Page<PostSummaryResponse> byLatestComment = postsService.searchByTitle(forumId, title, pageable);
        return ResponseEntity.ok(byLatestComment);
    }

    /**
     * 搜尋內文。<br>
     * 依照 popularity 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/content/popularity")
    public ResponseEntity<Page<PostSummaryResponse>> searchByContentSortByPop(@PathVariable Integer forumId,
                                                                              @RequestParam String content,
                                                                              @PageableDefault(
                                                                                      sort = "popularityScore",
                                                                                      direction = Sort.Direction.DESC
                                                                              ) Pageable pageable) {
        Page<PostSummaryResponse> byPopularityScore = postsService.searchByContent(forumId, content, pageable);
        return ResponseEntity.ok(byPopularityScore);
    }

    /**
     * 搜尋內文。<br>
     * 依照 latestCommentAt 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/content/latestComment")
    public ResponseEntity<Page<PostSummaryResponse>> searchByContentSortByLatestC(@PathVariable Integer forumId,
                                                                                  @RequestParam String content,
                                                                                  @PageableDefault(
                                                                                          sort = "latestCommentAt",
                                                                                          direction = Sort
                                                                                                  .Direction
                                                                                                  .DESC
                                                                                  ) Pageable pageable) {
        Page<PostSummaryResponse> byPopularityScore = postsService.searchByContent(forumId, content, pageable);
        return ResponseEntity.ok(byPopularityScore);
    }

    /**
     * 搜尋內文。<br>
     * 依照 createdAt 排序。
     */
    @GetMapping("/api/{forumId}/posts/searchBy/content/createdAt")
    public ResponseEntity<Page<PostSummaryResponse>> searchByContentSortByLatestPost(@PathVariable Integer forumId,
                                                                                     @RequestParam String content,
                                                                                     @PageableDefault(
                                                                                             sort = "createdAt",
                                                                                             direction = Sort
                                                                                                     .Direction
                                                                                                     .DESC
                                                                                     ) Pageable pageable) {
        Page<PostSummaryResponse> byPopularityScore = postsService.searchByContent(forumId, content, pageable);
        return ResponseEntity.ok(byPopularityScore);
    }

    // ===== RETRIEVE ===== POST DETAIL ========================================

    /**
     * 文章內容。
     */
    @GetMapping("/api/post/{postId}/detail")
    public ResponseEntity<PostDetailsResponse> getPostDetailsByPostId(@PathVariable Long postId) {
        PostDetailsResponse postByPostId = postsService.findPostByPostId(postId);
        return ResponseEntity.ok(postByPostId);
    }

    // ===== UPDATE ========================================

    /**
     * 發文者自行更新文章。
     */
    @PutMapping("/api/post/{postId}/editPost")
    public ResponseEntity<Long> editPostByPostId(@MemberId Integer memberId,
                                                 @PathVariable Long postId,
                                                 @RequestBody PostUpdateRequest post) {
        Member member = memberService.findById(memberId);
        Long editedPostId = postsService.editPostByPostId(member, postId, post);
        return ResponseEntity.ok(editedPostId);
    }

    /**
     * Moderator 將文章加入 isRecommended。
     */
    @PutMapping("/api/post/{postId}/moderator/togglePostIsRecommended")
    public ResponseEntity<PostToggleRecommendResponse> toggleRecommendByPostId(@MemberId Integer memberId,
                                                                               @PathVariable Long postId) {
        Member moderator = memberService.findById(memberId);
        PostToggleRecommendResponse toggleResponse = postsService.toggleRecommendByPostId(moderator, postId);
        return ResponseEntity.ok(toggleResponse);
    }

    /**
     * Moderator 鎖定文章。<br>
     * 回傳 Post ID 讓前端重新刷新頁面。
     */
    @PutMapping("/api/post/{postId}/moderator/togglePostIsLocked")
    public ResponseEntity<PostToggleLockedResponse> toggleIsLockedByPostId(@MemberId Integer memberId,
                                                                           @PathVariable Long postId) {
        Member moderator = memberService.findById(memberId);
        PostToggleLockedResponse toggleResponse = postsService.toggleIsLockedByPostId(moderator, postId);
        return ResponseEntity.ok(toggleResponse);
    }

    // ===== DELETE ========================================

    /**
     * Moderator 鎖定並且刪除文章。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    @DeleteMapping("/api/post/{postId}/moderator/togglePostIsDeleted")
    public ResponseEntity<?> toggleIsDeletedByPostId(@MemberId Integer memberId,
                                                     @PathVariable Long postId) {
        Member moderator = memberService.findById(memberId);
        String message = postsService.toggleIsDeletedByPostId(moderator, postId);
        return ResponseEntity.ok(message);
    }

    /**
     * Member 鎖定並且刪除文章。<br>
     * 不可逆操作。<br>
     */
    @DeleteMapping("/api/post/{postId}/author/deletePostByPostId")
    public ResponseEntity<PostDeleteResponse> deletePostByPostId(@MemberId Integer memberId,
                                                                 @PathVariable Long postId) {
        Member member = memberService.findById(memberId);
        PostDeleteResponse deleteResponse = postsService.deletePostByPostIdAndMember(member, postId);
        return ResponseEntity.ok(deleteResponse);
    }

}