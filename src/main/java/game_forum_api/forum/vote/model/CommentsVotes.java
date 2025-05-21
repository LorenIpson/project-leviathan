package game_forum_api.forum.vote.model;

import game_forum_api.forum.comment.model.Comments;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 留言與回覆評分。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments_votes")
public class CommentsVotes {

    @EmbeddedId
    private CommentsVotesPK commentsVotesPK;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comments comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "vote")
    private Integer vote;

    @Column(name = "created_at")
    private LocalDateTime votedAt;

}
