package game_forum_api.forum.post.service;

import game_forum_api.avatar.model.AvatarPhoto;
import game_forum_api.avatar.repository.AvatarPhotoRepository;
import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.ban.repository.ForumsBansRepository;
import game_forum_api.forum.comment.repository.CommentsRepository;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.image.model.PostImages;
import game_forum_api.forum.image.repository.PostImagesRepository;
import game_forum_api.forum.pin.repository.ForumPinsRepository;
import game_forum_api.forum.post.dto.*;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.tag.model.ForumTags;
import game_forum_api.forum.tag.repository.ForumTagsRepository;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.view.repository.PostsViewsRepository;
import game_forum_api.forum.vote.repository.PostsVotesRepository;
import game_forum_api.member.model.Member;
import game_forum_api.points.service.PointsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PostsService {

    private final PostsRepository postsRepos;

    private final PostsVotesRepository postsVotesRepos;

    private final PostsViewsRepository postsViewsRepos;

    private final PostImagesRepository postImagesRepos;

    private final AvatarPhotoRepository avatarPhotoRepos;

    private final CommentsRepository commentsRepos;

    private final ForumsRepository forumRepos;

    private final ForumTagsRepository forumTagsRepos;

    private final ForumPinsRepository forumPinsRepos;

    private final ForumsBansRepository forumsBansRepos;

    private final PointsService pointsService;

    public PostsService(PostsRepository postsRepos,
                        PostsVotesRepository postsVotesRepos,
                        PostsViewsRepository postsViewsRepos,
                        AvatarPhotoRepository avatarPhotoRepos,
                        CommentsRepository commentsRepos,
                        ForumsRepository forumRepos,
                        ForumTagsRepository forumTagsRepos,
                        ForumPinsRepository forumPinsRepos,
                        PostImagesRepository postImagesRepos,
                        ForumsBansRepository forumsBansRepos,
                        PointsService pointsService) {
        this.postsRepos = postsRepos;
        this.postsVotesRepos = postsVotesRepos;
        this.postsViewsRepos = postsViewsRepos;
        this.avatarPhotoRepos = avatarPhotoRepos;
        this.commentsRepos = commentsRepos;
        this.forumRepos = forumRepos;
        this.forumTagsRepos = forumTagsRepos;
        this.forumPinsRepos = forumPinsRepos;
        this.postImagesRepos = postImagesRepos;
        this.forumsBansRepos = forumsBansRepos;
        this.pointsService = pointsService;
    }

    // ===== CREATE ========================================

    // @Transactional
    public Long createNewPostByForumId(Member member, Integer forumId, PostCreateRequest postDTO) {

        Forums targetForum = forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

//        List<Member> forumsBans = targetForum.getForumsBans().stream().map(ForumsBans::getMember).toList();
//        boolean isBanned = forumsBans.contains(member);
//        if (isBanned) {
//            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
//        }

        ForumsBans ban = forumsBansRepos
                .findFirstByForumAndMemberOrderByBanIdDesc(targetForum, member).orElse(null);
        if (ban != null) {
            Boolean isPenalized = ban.getIsPenalized();
            if (isPenalized) {
                throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
            }
        }

        Posts newPost = new Posts();
        newPost.setForum(targetForum);
        newPost.setMember(member);
        newPost.setTitle(postDTO.getTitle());

        List<Long> tagIds = postDTO.getTagsIds();
        List<ForumTags> tags = (tagIds == null || tagIds.isEmpty())
                ? Collections.emptyList() : forumTagsRepos.findAllById(tagIds);
        newPost.setPostTags(tags);

        newPost.setContent(postDTO.getContent());
        newPost.setSpoiler(postDTO.getSpoiler());
        newPost.setCreatedAt(LocalDateTime.now());
        //  newPost.setLatestCommentAt(null);
        newPost.setLatestCommentAt(LocalDateTime.now());
        newPost.setIsEdited(false);
        newPost.setEditedAt(LocalDateTime.now()); // 節省前端顯示發文時間的條件判斷邏輯 QQ
        newPost.setIsRecommended(false);
        newPost.setIsLocked(false);
        newPost.setIsDeleted(false);
        newPost.setPopularityScore(0L);
        postsRepos.save(newPost);

        Long postId = newPost.getId();
        List<PostImages> postImages = postImagesRepos.getPostImagesByMemberAndIsTemp(member, true);
        for (PostImages postImage : postImages) {
            postImage.setPost(newPost);
            postImage.setIsTemp(false);
            postImagesRepos.save(postImage);
        }

        pointsService.updatePoints(member.getId(), 10, "新文章獎勵！");

        return postId;

    }

    // ===== RETRIEVE ===== ALL POSTS ========================================

    /**
     * 依照發文時間排序。<br>
     * 回傳到 Post Summary。
     */
    public Page<PostSummaryResponse> sortByCreatedAt(Integer forumId, List<Long> tagId, Pageable pageable) {

        forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。" + forumId));

        List<Long> pinnedPostIds = forumPinsRepos.findByForum_Id(forumId)
                .stream()
                .map(pin -> pin.getPinnedPost().getId())
                .toList();
        if (pinnedPostIds.isEmpty()) {
            pinnedPostIds = List.of(-1L);
        }

        if (tagId == null) {
            Page<Posts> byCreatedAt = postsRepos.findByForum_IdAndIdNotIn(forumId, pinnedPostIds, pageable);
            return byCreatedAt.map(post -> {
                Long voteCount = postsVotesRepos.countVotesByPost(post.getId());
                Long viewCount = postsViewsRepos.countViewsByPost(post.getId());
                Long commentCount = commentsRepos.countCommentsByPostId(post.getId());
                return PostSummaryMapper.toPostSummaryResponseDTO(post, voteCount, viewCount, commentCount);
            });
        }

        List<ForumTags> tag = forumTagsRepos.findAllById(tagId);
        Page<Posts> allByPostTags = postsRepos
                .findPostsByForum_IdAndPostTagsAndIdNotIn(forumId, tag, pinnedPostIds, pageable);
        return allByPostTags.map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        ));

    }

    /**
     * 依照最新留言排序。<br>
     * 回傳到 Post Summary。
     */
    public Page<PostSummaryResponse> sortByLatestComment(Integer forumId, List<Long> tagId, Pageable pageable) {

        forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。"));

        List<Long> pinnedPostIds = forumPinsRepos.findByForum_Id(forumId)
                .stream()
                .map(pin -> pin.getPinnedPost().getId())
                .toList();
        if (pinnedPostIds.isEmpty()) {
            pinnedPostIds = List.of(-1L);
        }

        if (tagId == null) {
            Page<Posts> byLatestComment = postsRepos.findByForum_IdAndIdNotIn(forumId, pinnedPostIds, pageable);
            return byLatestComment.map(post -> {
                Long voteCount = postsVotesRepos.countVotesByPost(post.getId());
                Long viewCount = postsViewsRepos.countViewsByPost(post.getId());
                Long commentCount = commentsRepos.countCommentsByPostId(post.getId());
                return PostSummaryMapper.toPostSummaryResponseDTO(post, voteCount, viewCount, commentCount);
            });
        }

        List<ForumTags> tag = forumTagsRepos.findAllById(tagId);
        Page<Posts> allByPostTags = postsRepos
                .findPostsByForum_IdAndPostTagsAndIdNotIn(forumId, tag, pinnedPostIds, pageable);
        return allByPostTags.map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        ));

    }

    /**
     * 依照最高評分排序。<br>
     * 回傳到 Post Summary。
     */
    public Page<PostSummaryResponse> sortByVoteScore(Integer forumId, List<Long> tagId, Pageable pageable) {

        forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。"));

        List<Long> pinnedPostIds = forumPinsRepos.findByForum_Id(forumId)
                .stream()
                .map(pin -> pin.getPinnedPost().getId())
                .toList();
        if (pinnedPostIds.isEmpty()) {
            pinnedPostIds = List.of(-1L);
        }

        if (tagId == null) {
            Page<Posts> byVoteScore = postsRepos.findByForum_IdAndIdNotIn(forumId, pinnedPostIds, pageable);
            return byVoteScore.map(post -> {
                Long voteCount = postsVotesRepos.countVotesByPost(post.getId());
                Long viewCount = postsViewsRepos.countViewsByPost(post.getId());
                Long commentCount = commentsRepos.countCommentsByPostId(post.getId());
                return PostSummaryMapper.toPostSummaryResponseDTO(post, voteCount, viewCount, commentCount);
            });
        }

        List<ForumTags> tag = forumTagsRepos.findAllById(tagId);
        Page<Posts> allByPostTags = postsRepos
                .findPostsByForum_IdAndPostTagsAndIdNotIn(forumId, tag, pinnedPostIds, pageable);
        return allByPostTags.map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        ));

    }

    // ===== RETRIEVE ===== PINNED POSTS ========================================

    /**
     * 取出置頂文章。
     */
    public List<PostSummaryResponse> findPinnedPosts(Integer forumId) {

        forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<Long> pinnedPostIds = forumPinsRepos.findByForum_Id(forumId)
                .stream()
                .map(pin -> pin.getPinnedPost().getId())
                .toList();
        if (pinnedPostIds.isEmpty()) {
            pinnedPostIds = List.of(-1L);
        }

        List<Posts> pinnedPosts = postsRepos.findByForum_IdAndIdIn(forumId, pinnedPostIds);
        return pinnedPosts.stream().map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        )).toList();

    }

    // ===== RETRIEVE ===== POSTS BY SEARCH RESULTS ========================================

    /**
     * 透過標題查詢文章。<br>
     * Controller 決定排序方式。
     */
    public Page<PostSummaryResponse> searchByTitle(Integer forumId, String title, Pageable pageable) {

        Forums targetForum = forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        Page<Posts> searchResult = postsRepos
                .findPostsByForumAndTitleContainsIgnoreCase(targetForum, title, pageable);

        return searchResult.map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        ));

    }

    /**
     * 透過內文查詢文章。<br>
     * Controller 決定排序方式。
     */
    public Page<PostSummaryResponse> searchByContent(Integer forumId, String content, Pageable pageable) {

        Forums targetForum = forumRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        Page<Posts> searchResult = postsRepos.findPostsByForumAndContentContaining(targetForum, content, pageable);

        return searchResult.map(post -> PostSummaryMapper.toPostSummaryResponseDTO(
                post,
                postsVotesRepos.countVotesByPost(post.getId()),
                postsViewsRepos.countViewsByPost(post.getId()),
                commentsRepos.countCommentsByPostId(post.getId())
        ));

    }

    // ===== RETRIEVE ===== POST DETAIL ========================================

    /**
     * 回傳到 Post Detail。
     */
    public PostDetailsResponse findPostByPostId(Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        Long voteCount = postsVotesRepos.countVotesByPost(targetPost.getId());
        Long viewCount = postsViewsRepos.countViewsByPost(targetPost.getId());
        Long commentCount = commentsRepos.countCommentsByPostId(targetPost.getId());

        AvatarPhoto avatarObj = avatarPhotoRepos.findByMemberId(targetPost.getMember().getId()).orElse(null);
        if (avatarObj != null) {
            byte[] bodyPhoto = avatarObj.getBodyPhoto();
            return PostDetailsMapper.toPostResponseDTO(targetPost, bodyPhoto, voteCount, viewCount, commentCount);
        }

        return PostDetailsMapper.toPostResponseDTO(targetPost, null, voteCount, viewCount, commentCount);

    }

    // ===== UPDATE ========================================

    /**
     * 發文者自行更新文章。
     */
    // @Transactional
    public Long editPostByPostId(Member member, Long postId, PostUpdateRequest postDTO) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        if (!targetPost.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

//        List<Member> forumsBans = targetPost.getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
//        boolean isBanned = forumsBans.contains(member);
//        if (isBanned) {
//            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
//        }

        ForumsBans ban = forumsBansRepos
                .findFirstByForumAndMemberOrderByBanIdDesc(targetPost.getForum(), member).orElse(null);
        if (ban != null) {
            Boolean isPenalized = ban.getIsPenalized();
            if (isPenalized) {
                throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
            }
        }

        targetPost.setTitle(postDTO.getTitle());
        targetPost.setContent(postDTO.getContent());
        targetPost.setSpoiler(postDTO.getSpoiler());

        List<Long> tagIds = postDTO.getTagsIds();
        List<ForumTags> tags = (tagIds == null || tagIds.isEmpty())
                ? Collections.emptyList() : forumTagsRepos.findAllById(tagIds);
        targetPost.setPostTags(tags);

        targetPost.setEditedAt(LocalDateTime.now());
        targetPost.setIsEdited(true);
        postsRepos.save(targetPost);

        List<PostImages> postImages = postImagesRepos.getPostImagesByMemberAndIsTemp(member, true);
        for (PostImages postImage : postImages) {
            postImage.setPost(targetPost);
            postImage.setIsTemp(false);
            postImagesRepos.save(postImage);
        }

        return targetPost.getId();

    }

    /**
     * Moderator 將文章加入 isRecommended。
     */
    public PostToggleRecommendResponse toggleRecommendByPostId(Member moderator, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        List<Member> moderators = targetPost.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetPost.getIsDeleted();
        if (isDeleted) {
            throw new DataConflictException("文章已被刪除。");
        }

        Boolean isRecommended = targetPost.getIsRecommended();
        targetPost.setIsRecommended(isRecommended == null || !isRecommended);
        postsRepos.save(targetPost);
        return new PostToggleRecommendResponse(targetPost.getId(), targetPost.getIsRecommended());

    }

    /**
     * Moderator 鎖定文章。<br>
     * 回傳 Post ID 讓前端重新刷新頁面。
     */
    public PostToggleLockedResponse toggleIsLockedByPostId(Member moderator, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        List<Member> moderators = targetPost.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetPost.getIsDeleted();
        if (isDeleted) {
            throw new DataConflictException("文章已被刪除。");
        }

        Boolean isLocked = targetPost.getIsLocked();
        if (isLocked) {
            targetPost.setIsLocked(false);
            targetPost.setLockedBy(null);
            postsRepos.save(targetPost);
            return new PostToggleLockedResponse(targetPost.getId(), targetPost.getIsLocked());
        } else {
            targetPost.setIsLocked(true);
            targetPost.setLockedBy(moderator);
            postsRepos.save(targetPost);
            return new PostToggleLockedResponse(targetPost.getId(), targetPost.getIsLocked());
        }

    }

    /**
     * 排程更新 Popularity Score。<br>
     * 與 MailBox 一樣在凌晨 2:00。
     */
    // @Transactional
    public void updatePostPopularityScore() {
        LocalDateTime last24HoursAgo = LocalDateTime.now().minusHours(24);
        List<Posts> posts = postsRepos.findAll();

        for (Posts post : posts) {
            Long postId = post.getId();
            Long comments = commentsRepos.countCommentsByPostIdInLast24Hours(postId, last24HoursAgo);
            Long votes = postsVotesRepos.countVotesByPostInLast24Hours(postId, last24HoursAgo);
            Long views = postsViewsRepos.countViewsByPostInLast24Hours(postId, last24HoursAgo);

            Long finalScore = (votes * 73) + (views * 103) + (comments * 69);
            post.setPopularityScore(finalScore);
        }

        postsRepos.saveAll(posts);

    }

    // ===== DELETE ========================================

    /**
     * Moderator 鎖定並且刪除文章。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    public String toggleIsDeletedByPostId(Member moderator, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        List<Member> moderators = targetPost.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetPost.getIsDeleted();
        if (isDeleted != null && isDeleted) {
            targetPost.setIsDeleted(false);
            targetPost.setDeletedBy(null);
            postsRepos.save(targetPost);
            return "文章解除刪除狀態";
        } else {
            targetPost.setIsRecommended(false);
            targetPost.setIsLocked(true);
            targetPost.setLockedBy(moderator);
            targetPost.setIsDeleted(true);
            targetPost.setDeletedBy(moderator);
            postsRepos.save(targetPost);
            return "文章已鎖定並且刪除";
        }

    }

    /**
     * Member 鎖定並且刪除文章。<br>
     * 不可逆操作。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    public PostDeleteResponse deletePostByPostIdAndMember(Member author, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        boolean isAuthor = targetPost.getMember().equals(author);
        if (!isAuthor) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetPost.getIsDeleted();
        if (isDeleted) {
            throw new DataConflictException("文章已被刪除。");
        }

        targetPost.setIsRecommended(false);
        targetPost.setIsLocked(true);
        targetPost.setLockedBy(author);
        targetPost.setIsDeleted(true);
        targetPost.setDeletedBy(author);
        postsRepos.save(targetPost);

        return new PostDeleteResponse(targetPost.getId(), targetPost.getIsDeleted(), "成功刪除文章。");

    }

}