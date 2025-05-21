package game_forum_api.forum.ban.model;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 討論區懲罰。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forums_bans")
public class ForumsBans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private Integer banId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    private Forums forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 討論區懲罰會員裁決人。<br>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banned_by")
    private Member bannedBy;

    @Column(name = "banned_at", nullable = false)
    private LocalDateTime bannedAt;

    @Column(name = "ban_reason", nullable = false, length = 500)
    private String banReason;

    @Column(name = "banned_til", nullable = false)
    private LocalDateTime bannedTil;

    @Column(name = "is_penalized", nullable = false)
    private Boolean isPenalized;

}
