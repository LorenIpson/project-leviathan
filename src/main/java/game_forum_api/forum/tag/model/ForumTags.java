package game_forum_api.forum.tag.model;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.post.model.Posts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 討論區標籤。<br>
 * 每個討論板擁有獨立的標籤池。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forum_tags")
public class ForumTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forums forum;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // ===== POSTS =========================

    @ManyToMany(mappedBy = "postTags")
    private List<Posts> posts = new ArrayList<>();

}
