package game_forum_api.forum.forum.service;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.exception.common.UnauthorizedException;
import game_forum_api.forum.category.model.Categories;
import game_forum_api.forum.category.repository.CategoriesRepository;
import game_forum_api.forum.comment.repository.CommentsRepository;
import game_forum_api.forum.forum.dto.*;
import game_forum_api.forum.forum.model.ForumDetail;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumDetailRepository;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.view.repository.PostsViewsRepository;
import game_forum_api.forum.vote.repository.PostsVotesRepository;
import game_forum_api.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ForumsService {

    private final ForumsRepository forumsRepos;

    private final ForumDetailRepository forumDetailRepos;

    private final PostsRepository postsRepos;

    private final PostsViewsRepository postsViewsRepos;

    private final PostsVotesRepository postsVotesRepos;

    private final CommentsRepository commentsRepos;

    private final CategoriesRepository categoriesRepos;

    public ForumsService(ForumsRepository forumsRepos,
                         ForumDetailRepository forumDetailRepos,
                         PostsRepository postsRepos,
                         PostsViewsRepository postsViewsRepos,
                         PostsVotesRepository postsVotesRepos,
                         CommentsRepository commentsRepos,
                         CategoriesRepository categoriesRepos) {
        this.forumsRepos = forumsRepos;
        this.forumDetailRepos = forumDetailRepos;
        this.postsRepos = postsRepos;
        this.postsViewsRepos = postsViewsRepos;
        this.postsVotesRepos = postsVotesRepos;
        this.commentsRepos = commentsRepos;
        this.categoriesRepos = categoriesRepos;
    }

    // ===== CREATE ========================================

    /**
     * 新增新的 Forum。<br>
     * 需要 Admin 權限。
     */
    // @Transactional
    public ForumCreateResponse createNewForums(Member admin, ForumRequest forumDTO) {

        boolean isAdmin = admin.getRole() == 3;
        if (!isAdmin) {
            throw new UnauthorizedException("使用者沒有操作權限。");
        }

        Forums newForum = new Forums();
        newForum.setName(forumDTO.getName());

        try {
            newForum.setCover(forumDTO.getMainCover().getBytes());
        } catch (IOException e) {
            throw new BadRequestException("圖片上傳失敗。");
        }

        List<Categories> categories = forumDTO.getCategoryIds().stream()
                .map(id -> categoriesRepos.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("找不到目標分類，IDs：" + id)))
                .collect(Collectors.toList());

        newForum.setCategories(categories);
        newForum.setIsActive(true);
        newForum.setIsVisible(true);
        newForum.setCreatedAt(LocalDateTime.now());
        newForum.setPopularityScore(0L);
        forumsRepos.save(newForum);

        ForumDetail newForumDetail = new ForumDetail();
        newForumDetail.setForum(newForum);
        newForumDetail.setDescription(forumDTO.getDescription());

        try {
            newForumDetail.setCover(forumDTO.getSecondaryCover().getBytes());
        } catch (IOException e) {
            throw new BadRequestException("圖片上傳失敗。");
        }

        forumDetailRepos.save(newForumDetail);

        return ForumCreateMapper.toForumCreateResponse(newForum);

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有的 Forum 到 Forum Summary。
     */
    public List<ForumResponse> findAllForums() {

        List<Forums> forums = forumsRepos.findAll();
        return ForumMapper.toForumsListResponseDTO(forums);

    }

    /**
     * 依照熱門度排序。
     */
    public Page<ForumResponse> findAllForumsSortByPopularity(Pageable pageable) {

        Page<Forums> forumsByPopularity = forumsRepos.findAll(pageable);
        return forumsByPopularity.map(ForumMapper::toForumResponseDTO);

    }

    /**
     * 使用 Category 篩選討論區。<br>
     * 其實只支援單一個條件過濾。
     */
    public Page<ForumSummaryResponse> findForumsByPopularityAndTag(String forumName, List<Integer> categoryId, Pageable pageable) {

        if (forumName != null && !forumName.isEmpty()) {
            System.out.println("Hehe not null");
            Page<Forums> forumsByNameContainsIgnoreCase = forumsRepos.findForumsByNameContainsIgnoreCase(forumName, pageable);
            return forumsByNameContainsIgnoreCase.map(forum -> {
                Posts topPost = postsRepos.findFirstByForumOrderByPopularityScoreDesc(forum).orElse(null);
                Long topPostId = topPost != null ? topPost.getId() : -1L;
                String topPostTitle = topPost != null ? topPost.getTitle() : "（無熱門文章）";
                return ForumSummaryMapper.toForumSummaryResponseDTO(forum, topPostId, topPostTitle);
            });
        }

        if (categoryId == null || categoryId.isEmpty()) {
            System.out.println("Hehe null");
            Page<Forums> forumsByPopularity = forumsRepos.findAll(pageable);
            return forumsByPopularity.map(forum -> {
                Posts topPost = postsRepos.findFirstByForumOrderByPopularityScoreDesc(forum).orElse(null);
                Long topPostId = topPost != null ? topPost.getId() : -1L;
                String topPostTitle = topPost != null ? topPost.getTitle() : "（無熱門文章）";
                return ForumSummaryMapper.toForumSummaryResponseDTO(forum, topPostId, topPostTitle);
            });
        }

        List<Categories> allById = categoriesRepos.findAllById(categoryId);
        Page<Forums> forumsByPopularityAndTags = forumsRepos.findAllByCategories(allById, pageable);

        return forumsByPopularityAndTags.map(forum -> {
            Posts topPost = postsRepos.findFirstByForumOrderByPopularityScoreDesc(forum).orElse(null);
            Long topPostId = topPost != null ? topPost.getId() : -1L;
            String topPostTitle = topPost != null ? topPost.getTitle() : "（無熱門文章）";
            return ForumSummaryMapper.toForumSummaryResponseDTO(forum, topPostId, topPostTitle);
        });

    }

    // ===== UPDATE ========================================

    /**
     * 更新討論板封面。<br>
     * 需要 Admin 權限。
     */
    public String editForumCover(Member admin, Integer id, ForumRequest forumDTO) {

        boolean isAdmin = admin.getRole() == 3;
        if (!isAdmin) {
            throw new RuntimeException("使用者沒有操作權限。");
        }

        Forums targetForum = forumsRepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區，ID：" + id));

        try {
            targetForum.setCover(forumDTO.getMainCover().getBytes());
        } catch (IOException e) {
            throw new BadRequestException("圖片上傳失敗。");
        }
        forumsRepos.save(targetForum);
        return "封面更新成功";

    }

    /**
     * 排程更新 Popularity Score。<br>
     * 與 MailBox 一樣在凌晨 2:00。
     */
    // @Transactional
    public void updateForumsPopularityScore() {

        LocalDateTime last24HoursAgo = LocalDateTime.now().minusHours(24);
        List<Forums> forums = forumsRepos.findAll();

        for (Forums forum : forums) {
            Integer forumId = forum.getId();

            Long comments = commentsRepos.countCommentsByForumInLast24Hours(forumId, last24HoursAgo);
            Long votes = postsVotesRepos.countVotesByForumInLast24Hours(forumId, last24HoursAgo);
            Long views = postsViewsRepos.countViewsByForumInLast24Hours(forumId, last24HoursAgo);

            Long finalScore = (comments * 69) + (votes * 73) + (views * 100);
            forum.setPopularityScore(finalScore);
        }

        forumsRepos.saveAll(forums);

    }

}