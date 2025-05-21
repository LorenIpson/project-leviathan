package game_forum_api.forum.pin.service;

import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.pin.model.ForumPostsPins;
import game_forum_api.forum.pin.repository.ForumPinsRepository;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ForumPinsService {

    private final ForumPinsRepository forumPinsRepos;

    private final PostsRepository postsRepos;

    public ForumPinsService(ForumPinsRepository forumPinsRepos, PostsRepository postsRepos) {
        this.forumPinsRepos = forumPinsRepos;
        this.postsRepos = postsRepos;
    }

    // ===== CREATE ========================================

    public String addToForumPins(Member moderator, Long PostId) {


        Posts targetPost = postsRepos.findById(PostId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + PostId));

        List<Member> moderators = targetPost.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        boolean isDeleted = targetPost.getIsDeleted();
        if (isDeleted) {
            throw new DataConflictException("文章已被刪除。");
        }

        boolean isPinned = forumPinsRepos.existsByPinnedPost(targetPost);
        if (isPinned) {
            throw new DataConflictException("文章已經被置頂。");
        }

        long countPinnedPosts = forumPinsRepos.countByForum(targetPost.getForum());
        if (countPinnedPosts > 5) {
            throw new DataConflictException("置頂文章數超過 5 篇上限。");
        }

        ForumPostsPins newPin = new ForumPostsPins();
        newPin.setForum(targetPost.getForum());
        newPin.setPinnedPost(targetPost);
        newPin.setPinnedAt(LocalDateTime.now());
        forumPinsRepos.save(newPin);

        return "成功加入置頂文章。";

    }

    // ===== DELETE ========================================

    public String removeFromForumPins(Member moderator, Long PostId) {

        Posts targetPost = postsRepos.findById(PostId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + PostId));

        List<Member> moderators = targetPost.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        boolean isPinned = forumPinsRepos.existsByPinnedPost(targetPost);
        if (!isPinned) {
            throw new DataConflictException("文章沒有被置頂。");
        }

        List<ForumPostsPins> byPinnedPost = forumPinsRepos.findByPinnedPost(targetPost);

        forumPinsRepos.delete(byPinnedPost.getFirst());

        return "成功移出置頂文章。";

    }

}