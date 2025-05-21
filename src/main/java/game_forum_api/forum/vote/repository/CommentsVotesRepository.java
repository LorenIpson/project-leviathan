package game_forum_api.forum.vote.repository;

import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.vote.model.CommentsVotes;
import game_forum_api.forum.vote.model.CommentsVotesPK;
import game_forum_api.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsVotesRepository extends JpaRepository<CommentsVotes, CommentsVotesPK> {

    @Query("SELECT SUM(CASE WHEN cv.vote = 1 THEN 1 WHEN cv.vote = -1 THEN -1 ELSE 0 END) FROM CommentsVotes cv WHERE cv.comment.id= :commentId")
    Long countVotesByComment(Long commentId);

    List<CommentsVotes> findByCommentAndMember(Comments comment, Member member);

    List<CommentsVotes> findByComment_Post_IdAndMember(Long commentPostId, Member member);
}
