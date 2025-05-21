package game_forum_api.forum.comment.service;

import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.ban.repository.ForumsBansRepository;
import game_forum_api.forum.comment.dto.*;
import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.comment.repository.CommentsRepository;
import game_forum_api.forum.image.model.CommentImages;
import game_forum_api.forum.image.repository.CommentImageRepository;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.vote.repository.CommentsVotesRepository;
import game_forum_api.member.model.Member;
import game_forum_api.notification.service.NotificationService;
import game_forum_api.points.service.PointsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentsService {

    private final CommentsRepository commentsRepos;

    private final CommentsVotesRepository commentsVotesRepository;

    private final CommentImageRepository commentImageRepos;

    private final PostsRepository postsRepos;

    private final ForumsBansRepository forumsBansRepos;

    private final NotificationService notificationService;

    private final PointsService pointsService;

    public CommentsService(CommentsRepository commentsRepos,
                           PostsRepository postsRepos,
                           CommentsVotesRepository commentsVotesRepository,
                           CommentImageRepository commentImageRepos,
                           ForumsBansRepository forumsBansRepos,
                           NotificationService notificationService,
                           PointsService pointsService) {
        this.commentsRepos = commentsRepos;
        this.postsRepos = postsRepos;
        this.commentsVotesRepository = commentsVotesRepository;
        this.commentImageRepos = commentImageRepos;
        this.forumsBansRepos = forumsBansRepos;
        this.notificationService = notificationService;
        this.pointsService = pointsService;
    }

    // ===== CREATE ========================================

    /**
     * 新增一筆父級留言。
     */
    // @Transactional
    public CommentResponse createNewCommentByPostId(Member member,
                                                    Long postId,
                                                    CommentRequest commentDTO) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        if (targetPost.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetPost.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

        Integer role = member.getRole();
        if (role == 0) {
            throw new ForbiddenException("停權中無法操作。");
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

        Comments newComment = new Comments();
        newComment.setPost(targetPost);
        newComment.setMember(member);
        newComment.setParentComment(null);
        newComment.setContent(commentDTO.getContent());

        if (commentDTO.getImageId() != null) {
            CommentImages targetImg = commentImageRepos.findById(commentDTO.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到目標圖片。"));

            if (!targetImg.getMember().getId().equals(member.getId())) {
                throw new SecurityException("不能使用別人的圖片");
            }
            if (targetImg.getComment() != null || !targetImg.getIsTemp()) {
                throw new DataConflictException("圖片已被使用");
            }
            targetImg.setComment(newComment);
            targetImg.setIsTemp(false);
            commentImageRepos.save(targetImg);

            newComment.setCommentImages(targetImg);
        }

        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setIsEdited(false);
        newComment.setEditedAt(LocalDateTime.now());
        newComment.setIsLocked(false);
        newComment.setLockedBy(null);
        newComment.setIsDeleted(false);
        newComment.setDeletedBy(null);
        commentsRepos.save(newComment);

        targetPost.setLatestCommentAt(LocalDateTime.now());
        postsRepos.save(targetPost);

        notificationService.createNotification(
                targetPost.getMember().getId(),
                "post",
                "貼文有一則新回覆。",
                targetPost.getId().toString()
        ); // TODO: Value: post，跳轉到 /post/{postId}。

        pointsService.updatePoints(member.getId(), 5, "回覆文章獎勵！");

        return CommentMapper.toCommentResponse(newComment);

    }

    /**
     * 新增一筆子級留言。
     */
    public CommentResponse createNewCommentByParentCommentId(Member member,
                                                             Long postId,
                                                             Long parentCommentId,
                                                             CommentRequest commentDTO) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        if (targetPost.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetPost.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

        Comments targetParentComment = commentsRepos.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標留言。ID：" + parentCommentId));

        if (targetParentComment.getIsLocked() || targetParentComment.getIsDeleted()) {
            throw new ResourceNotFoundException("目標留言已經刪除。");
        }

        List<Member> forumsBans = targetPost.getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
        boolean isBanned = forumsBans.contains(member);
        if (isBanned) {
            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
        }

        Comments newComment = new Comments();
        newComment.setPost(targetPost);
        newComment.setMember(member);
        newComment.setParentComment(targetParentComment);
        newComment.setContent(commentDTO.getContent());

        if (commentDTO.getImageId() != null) {
            CommentImages targetImg = commentImageRepos.findById(commentDTO.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到目標圖片。"));
            if (!targetImg.getMember().getId().equals(member.getId())) {
                throw new SecurityException("不能使用別人的圖片");
            }
            if (targetImg.getComment() != null || !targetImg.getIsTemp()) {
                throw new IllegalStateException("圖片已被使用");
            }
            targetImg.setComment(newComment);
            targetImg.setIsTemp(false);
            commentImageRepos.save(targetImg);

            newComment.setCommentImages(targetImg);
        }

        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setIsEdited(false);
        newComment.setEditedAt(LocalDateTime.now());
        newComment.setIsLocked(false);
        newComment.setLockedBy(null);
        newComment.setIsDeleted(false);
        newComment.setDeletedBy(null);
        commentsRepos.save(newComment);

        targetPost.setLatestCommentAt(LocalDateTime.now());
        postsRepos.save(targetPost);

        notificationService.createNotification(
                targetParentComment.getMember().getId(),
                "post",
                "回覆有一則新留言。",
                targetPost.getId().toString()
        );

        pointsService.updatePoints(member.getId(), 5, "回覆文章獎勵！");

        return CommentMapper.toCommentResponse(newComment);

    }

// ===== RETRIEVE ========================================

    /**
     * 查詢 Post ID 所有樹狀留言。
     */
    public List<CommentNestResponse> getCommentsByPostId(Long postId) {

        postsRepos.findById(postId).orElseThrow(() -> new ResourceNotFoundException("目標文章不存在。ID：" + postId));

        List<Comments> topLevelComments = commentsRepos.findTopLevelComments(postId);
        return topLevelComments.stream()
                .map(this::buildReplies)
                .collect(Collectors.toList());

    }

    /**
     * 父級留言搜尋功能。
     */
    public List<CommentNestResponse> getCommentsBySearch(Long postId, String keyword) {

        postsRepos.findById(postId).orElseThrow(() -> new ResourceNotFoundException("目標文章不存在。ID：" + postId));

        List<Comments> topLevelCommentsBySearch = commentsRepos.findTopLevelCommentsBySearch(postId, keyword);
        return topLevelCommentsBySearch.stream()
                .map(this::buildReplies)
                .collect(Collectors.toList());

    }

    private CommentNestResponse buildReplies(Comments comment) {

        List<Comments> replies = commentsRepos.findRepliesByParentId(comment.getId());
        Long voteCount = commentsVotesRepository.countVotesByComment(comment.getId());
        List<CommentNestResponse> repliesList = replies.stream()
                .map(this::buildReplies)
                .collect(Collectors.toList());

        return CommentNestMapper.toCommentNestResponse(comment, repliesList, voteCount);

    }

// ===== UPDATE ========================================

    /**
     * 發文者自行更新留言。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    public String editCommentByCommentId(Member member, Long commentId, CommentRequest commentDTO) {

        Comments targetComment = commentsRepos.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標留言。ID：" + commentId));

        if (!targetComment.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("使用者沒有操作權限Hehe。");
        }

        if (commentDTO.getImageId() != null) {
            CommentImages targetImg = commentImageRepos.findById(commentDTO.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到目標圖片。"));
            if (!targetImg.getMember().getId().equals(member.getId())) {
                throw new SecurityException("不能使用別人的圖片");
            }
            if (targetImg.getComment() != null || !targetImg.getIsTemp()) {
                throw new IllegalStateException("圖片已被使用");
            }

            boolean oldImageIsPresent = commentImageRepos.existsByComment_Id(commentId);
            if (oldImageIsPresent) {
                CommentImages oldImage = commentImageRepos.findByComment_Id(commentId).getFirst();
                oldImage.setComment(null);
                oldImage.setIsTemp(true);
                commentImageRepos.save(oldImage);
            }

            targetImg.setComment(targetComment);
            targetImg.setIsTemp(false);
            commentImageRepos.save(targetImg);

            targetComment.setCommentImages(targetImg);
        }

        targetComment.setContent(commentDTO.getContent());
        targetComment.setEditedAt(LocalDateTime.now());
        targetComment.setIsEdited(true);
        commentsRepos.save(targetComment);
        return "更新成功";

    }

// ===== DELETE ========================================

    /**
     * Moderator 鎖定並且刪除留言。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    public String toggleDeletedByCommentId(Member moderator, Long commentId) {

        Comments targetComment = commentsRepos.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標留言。ID：" + commentId));

        List<Member> moderators = targetComment.getPost().getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetComment.getIsDeleted();
        if (isDeleted != null && isDeleted) {
            targetComment.setIsLocked(false);
            targetComment.setLockedBy(null);
            targetComment.setIsDeleted(false);
            targetComment.setDeletedBy(null);
            commentsRepos.save(targetComment);
            return "留言解除刪除";
        } else {
            targetComment.setIsLocked(true);
            targetComment.setLockedBy(moderator);
            targetComment.setIsDeleted(true);
            targetComment.setDeletedBy(moderator);
            commentsRepos.save(targetComment);
            return "留言刪除成功";
        }

    }

    /**
     * Member 鎖定並且刪除留言。<br>
     * 不可逆操作。<br>
     * 回傳 String 讓前端顯示 Message 並重新刷新頁面。
     */
    public CommentDeleteResponse deleteCommentByCommentIdAndMember(Member author, Long commentId) {

        Comments targetComment = commentsRepos.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標留言。ID：" + commentId));

        boolean isAuthor = targetComment.getMember().equals(author);
        if (!isAuthor) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isDeleted = targetComment.getIsDeleted();
        if (isDeleted) {
            throw new DataConflictException("留言已被刪除。");
        }

        targetComment.setIsLocked(true);
        targetComment.setLockedBy(author);
        targetComment.setIsDeleted(true);
        targetComment.setDeletedBy(author);
        commentsRepos.save(targetComment);

        return new CommentDeleteResponse(targetComment.getPost().getId(),
                targetComment.getId(), targetComment.getIsDeleted(), "成功刪除留言。");

    }

}
