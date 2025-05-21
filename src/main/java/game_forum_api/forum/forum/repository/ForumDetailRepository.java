package game_forum_api.forum.forum.repository;

import game_forum_api.forum.forum.model.ForumDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumDetailRepository extends JpaRepository<ForumDetail, Integer> {
}
