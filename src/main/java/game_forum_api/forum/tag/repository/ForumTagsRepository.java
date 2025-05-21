package game_forum_api.forum.tag.repository;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.tag.model.ForumTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumTagsRepository extends JpaRepository<ForumTags, Long> {
    List<ForumTags> findByForumAndIsActive(Forums forum, Boolean isActive);

    List<ForumTags> findByForumAndIsActiveOrderByColorAsc(Forums forum, Boolean isActive);

    List<ForumTags> findByForumOrderByNameAsc(Forums forum);
}
