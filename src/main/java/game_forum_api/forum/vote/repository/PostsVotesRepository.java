package game_forum_api.forum.vote.repository;

import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.vote.model.PostsVotes;
import game_forum_api.forum.vote.model.PostsVotesPK;
import game_forum_api.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsVotesRepository extends JpaRepository<PostsVotes, PostsVotesPK> {

    @Query("SELECT SUM(CASE WHEN pv.vote = 1 THEN 1 WHEN pv.vote = -1 THEN -1 ELSE 0 END) FROM PostsVotes pv WHERE pv.post.id = :postId")
    Long countVotesByPost(Long postId);

    @Query("SELECT COUNT(v) FROM PostsVotes v WHERE v.post.forum.id = :forumId AND v.votedAt >= :last24Hours")
    Long countVotesByForumInLast24Hours(@Param("forumId") Integer forumId, @Param("last24Hours") LocalDateTime last24Hours);

    @Query("SELECT COUNT(v) FROM PostsVotes v WHERE v.post.id = :postId AND v.votedAt >= :last24Hours")
    Long countVotesByPostInLast24Hours(@Param("postId") Long postId, @Param("last24Hours") LocalDateTime last24Hours);

    List<PostsVotes> findByPostAndMember(Posts post, Member member);

}
