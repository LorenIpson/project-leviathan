package game_forum_api.forum.view.repository;

import game_forum_api.forum.view.model.PostsViews;
import game_forum_api.forum.view.model.PostsViewsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostsViewsRepository extends JpaRepository<PostsViews, PostsViewsPK> {

    @Query("SELECT COUNT(pv) FROM PostsViews pv WHERE pv.post.id = :postId AND pv.member.id = :memberId " +
           "AND pv.viewedAt >= :last24Hours")
    Long addRecentView(@Param("postId") Long postId,
                       @Param("memberId") Integer memberId,
                       @Param("last24Hours") LocalDateTime last24Hours);

    @Query("SELECT COUNT(pv) FROM PostsViews pv WHERE pv.post.id IN :postId")
    Long countViewsByPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(pv.post.id) " +
           "FROM Posts ps " +
           "LEFT JOIN PostsViews pv " +
           "ON ps.id = pv.post.id " +
           "WHERE ps.forum.id =:forumId AND pv.viewedAt >= :last24Hours")
    Long countViewsByForumInLast24Hours(@Param("forumId") Integer forumId,
                                        @Param("last24Hours") LocalDateTime last24Hours);

    @Query("SELECT COUNT(pv) FROM PostsViews pv WHERE pv.post.id IN :postId AND pv.viewedAt >=:last24Hours")
    Long countViewsByPostInLast24Hours(@Param("postId") Long postId, @Param("last24Hours") LocalDateTime last24Hours);

}
