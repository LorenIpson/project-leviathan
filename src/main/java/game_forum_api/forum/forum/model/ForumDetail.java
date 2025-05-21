package game_forum_api.forum.forum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("DefaultAnnotationParam")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forum_detail")
public class ForumDetail {

    @Id
    @Column(name = "forum_id")
    private Integer forumId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "forum_id")
    private Forums forum;

    @Column(name = "cover", nullable = true)
    private byte[] cover;

    @Column(name = "description", nullable = true)
    private String description;

}
