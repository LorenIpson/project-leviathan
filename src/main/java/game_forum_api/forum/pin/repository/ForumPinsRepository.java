package game_forum_api.forum.pin.repository;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.pin.model.ForumPostsPins;
import game_forum_api.forum.post.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPinsRepository extends JpaRepository<ForumPostsPins, Integer> {

    List<ForumPostsPins> findByPinnedPost(Posts pinnedPost);

    List<ForumPostsPins> findByForum_Id(Integer forumId);

    long countByForum(Forums forum);

    boolean existsByPinnedPost(Posts pinnedPost);

}
