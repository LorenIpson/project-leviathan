package game_forum_api.forum.vote.model;

import game_forum_api.forum.post.model.Posts;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章評分。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts_votes")
public class PostsVotes {

    @EmbeddedId
    private PostsVotesPK postsVotesPK;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Posts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "vote")
    private Integer vote;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

}
