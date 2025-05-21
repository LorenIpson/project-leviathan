package game_forum_api.forum.view.model;

import game_forum_api.forum.post.model.Posts;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 文章觀看紀錄。<br>
 * 只會記錄登入者的觀看紀錄。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts_views")
public class PostsViews {

    @EmbeddedId
    private PostsViewsPK postsViewsPK;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Posts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

}
