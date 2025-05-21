package game_forum_api.forum.comment.repository;

import game_forum_api.forum.comment.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Query("SELECT COUNT(*) FROM Comments WHERE post.id IN :postId")
    Long countCommentsByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comments c WHERE c.post.id = :postId AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comments> findTopLevelComments(@Param("postId") Long postId);

    @Query("SELECT c FROM Comments c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.content LIKE CONCAT('%',:keyword,'%') ORDER BY c.createdAt ASC")
    List<Comments> findTopLevelCommentsBySearch(@Param("postId") Long postId, @Param("keyword") String keyword);

    @Query("SELECT c FROM Comments c WHERE c.parentComment.id = :parentId ORDER BY c.createdAt ASC")
    List<Comments> findRepliesByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) " +
           "FROM Comments c " +
           "WHERE c.post.forum.id = :forumId " +
           "AND c.createdAt >= :last24Hours")
    Long countCommentsByForumInLast24Hours(@Param("forumId") Integer forumId, @Param("last24Hours") LocalDateTime last24Hours);

    @Query("SELECT COUNT(*) FROM Comments c WHERE c.post.id IN :postId AND c.createdAt >=:last24Hours")
    Long countCommentsByPostIdInLast24Hours(@Param("postId") Long postId, @Param("last24Hours") LocalDateTime last24Hours);

}
