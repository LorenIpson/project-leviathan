package game_forum_api.forum.favorite.service;

import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.favorite.dto.FavForumSummaryMapper;
import game_forum_api.forum.favorite.dto.IsFavResponse;
import game_forum_api.forum.favorite.model.ForumFav;
import game_forum_api.forum.favorite.model.ForumFavPK;
import game_forum_api.forum.favorite.repository.ForumFavRepository;
import game_forum_api.forum.forum.dto.ForumSummaryResponse;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForumFavService {

    private final ForumFavRepository forumFavRepos;

    private final ForumsRepository forumsRepos;

    private final PostsRepository postsRepos;

    private final MemberRepository memberRepos;

    public ForumFavService(ForumFavRepository forumFavRepos,
                           ForumsRepository forumsRepos,
                           PostsRepository postsRepos,
                           MemberRepository memberRepos) {
        this.forumFavRepos = forumFavRepos;
        this.forumsRepos = forumsRepos;
        this.postsRepos = postsRepos;
        this.memberRepos = memberRepos;
    }

    // ===== RETRIEVE ========================================

    /**
     * 取得使用者最愛的看板。
     */
    public Page<ForumSummaryResponse> findAllFavForumsByMember(Member member, Pageable pageable) {

        Page<ForumFav> allByMember = forumFavRepos.findAllByMember(member, pageable);
        return allByMember.map(forum -> {
            Posts topPost = postsRepos.findFirstByForumOrderByPopularityScoreDesc(forum.getForum()).orElse(null);
            Long topPostId = topPost != null ? topPost.getId() : -1L;
            String topPostTitle = topPost != null ? topPost.getTitle() : "（無熱門文章）";
            return FavForumSummaryMapper.toFavForumSummaryResponse(forum, topPostId, topPostTitle);
        });

    }

    /**
     * 取得使用者最愛看板狀態。
     */
    public IsFavResponse isFav(Member member, Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));
        boolean isFav = forumFavRepos.existsByMemberAndForum(member, targetForum);
        return new IsFavResponse(isFav);

    }

    // ===== UPDATE ========================================

    // @Transactional
    public String toggleForumFav(Member member, Integer forumId) {

        Member targetMember = memberRepos.findByAccountId(member.getAccountId())
                .orElseThrow(() -> new ForbiddenException("使用者沒有操作權限。"));

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        boolean isFav = forumFavRepos.existsByMemberAndForum(targetMember, targetForum);

        if (isFav) {
            forumFavRepos.deleteByForumAndMember(targetForum, targetMember);
            return "已移除喜愛看板 " + targetForum.getName() + "。";
        }

        ForumFavPK pk = new ForumFavPK();
        pk.setForumId(targetForum.getId());
        pk.setMemberId(targetMember.getId());

        ForumFav newFav = new ForumFav();
        newFav.setForumFavPK(pk);
        newFav.setMember(targetMember);
        newFav.setForum(targetForum);
        forumFavRepos.save(newFav);

        return "已新增至喜愛看板 " + targetForum.getName();

    }

}
