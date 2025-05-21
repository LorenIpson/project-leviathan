package game_forum_api.forum.comment.model;

import game_forum_api.forum.image.model.CommentImages;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.vote.model.CommentsVotes;
import game_forum_api.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 留言與回覆。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    /**
     * 留言與父級留言。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comments parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private Member lockedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private Member deletedBy;

    // ===== Votes =========================

    @OneToMany(mappedBy = "comment")
    private List<CommentsVotes> commentsVotes = new ArrayList<>();

    // ===== IMAGES =========================

    @OneToOne(mappedBy = "comment")
    private CommentImages commentImages;

}
