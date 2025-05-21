package game_forum_api.forum.post.repository;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.tag.model.ForumTags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

    Page<Posts> findPostsByForum_IdAndPostTagsAndIdNotIn(Integer forumId, List<ForumTags> postTags, List<Long> ids, Pageable pageable);

    Page<Posts> findByForum_IdAndIdNotIn(Integer forumId, List<Long> ids, Pageable pageable);

    List<Posts> findByForum_IdAndIdIn(Integer forumId, List<Long> postIds);

    Optional<Posts> findFirstByForumOrderByPopularityScoreDesc(Forums forum);

    Page<Posts> findPostsByForumAndTitleContainsIgnoreCase(Forums forum, String title, Pageable pageable);

    Page<Posts> findPostsByForumAndContentContaining(Forums forum, String content, Pageable pageable);

}
