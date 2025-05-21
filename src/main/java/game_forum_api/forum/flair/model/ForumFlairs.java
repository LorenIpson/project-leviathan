package game_forum_api.forum.flair.model;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 討論區使用者頭銜。<br>
 * 每個討論板擁有獨立的頭銜池。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forum_flairs")
public class ForumFlairs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flair_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forums forum;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * 每個使用者在各個討論區都可以有不同的頭銜。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
