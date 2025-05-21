package game_forum_api.forum.forum.repository;

import game_forum_api.forum.category.model.Categories;
import game_forum_api.forum.forum.model.Forums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumsRepository extends JpaRepository<Forums, Integer> {
    Page<Forums> findAllByCategories(List<Categories> categories, Pageable pageable);
    Page<Forums> findForumsByNameContainsIgnoreCase(String name, Pageable pageable);
}
