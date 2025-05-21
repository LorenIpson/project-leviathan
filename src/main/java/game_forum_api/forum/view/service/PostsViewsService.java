package game_forum_api.forum.view.service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.view.model.PostsViews;
import game_forum_api.forum.view.model.PostsViewsPK;
import game_forum_api.forum.view.repository.PostsViewsRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PostsViewsService {

    private final PostsViewsRepository postsViewsRepos;

    private final PostsRepository postsRepos;

    public PostsViewsService(PostsViewsRepository postsViewsRepos,
                             PostsRepository postsRepos) {
        this.postsViewsRepos = postsViewsRepos;
        this.postsRepos = postsRepos;
    }

    // ===== CREATE ========================================

    public void increaseViewsCount(Member member, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        Long viewed = postsViewsRepos.addRecentView(targetPost.getId(), member.getId(), last24Hours);
        if (viewed == 0 && !targetPost.getIsLocked() && !targetPost.getIsDeleted()) {
            PostsViewsPK newVotePK = new PostsViewsPK(postId, member.getId());
            PostsViews newViewRecord = new PostsViews();

            newViewRecord.setPostsViewsPK(newVotePK);
            newViewRecord.setPost(targetPost);
            newViewRecord.setMember(member);
            newViewRecord.setViewedAt(LocalDateTime.now());
            postsViewsRepos.save(newViewRecord);
        }

    }

}
