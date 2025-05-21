package game_forum_api.forum.pin.model;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.post.model.Posts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 討論區置頂文章。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forum_posts_pins")
public class ForumPostsPins {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pinned_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forums forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts pinnedPost;

    @Column(name = "pinned_at", nullable = false)
    private LocalDateTime pinnedAt;

}
