package game_forum_api.forum.flair.repository;

import game_forum_api.forum.flair.model.ForumFlairs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumFlairsRepository extends JpaRepository<ForumFlairs, Long> {
}
