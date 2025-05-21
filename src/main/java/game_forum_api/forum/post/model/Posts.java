package game_forum_api.forum.post.model;

import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.image.model.PostImages;
import game_forum_api.forum.pin.model.ForumPostsPins;
import game_forum_api.forum.tag.model.ForumTags;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.view.model.PostsViews;
import game_forum_api.forum.vote.model.PostsVotes;
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
 * 文章。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forums forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "spoiler", nullable = false)
    private Boolean spoiler;

    @Column(name = "popularity_score")
    private Long popularityScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "latest_comment_at")
    private LocalDateTime latestCommentAt;

    @Column(name = "is_edited", nullable = false) // Default 0
    private Boolean isEdited;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @Column(name = "is_recommended", nullable = false) // Default 0
    private Boolean isRecommended;

    @Column(name = "is_locked", nullable = false) // Default 0
    private Boolean isLocked;

    @Column(name = "is_deleted", nullable = false) // Default 0
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locked_by")
    private Member lockedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private Member deletedBy;

    // ===== TAGS =========================

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "posts_tags",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private List<ForumTags> postTags = new ArrayList<>();

    // ===== PINS =========================

    @OneToMany(mappedBy = "pinnedPost")
    private List<ForumPostsPins> forumPostsPins = new ArrayList<>();

    // ===== VIEWS =========================

    @OneToMany(mappedBy = "post")
    private List<PostsViews> postsViews = new ArrayList<>();

    // ===== VOTES =========================

    @OneToMany(mappedBy = "post")
    private List<PostsVotes> postsVotes = new ArrayList<>();

    // ===== COMMENTS =========================

    @OneToMany(mappedBy = "post")
    private List<Comments> comments = new ArrayList<>();

    // ===== IMAGES =========================

    @OneToMany(mappedBy = "post")
    private List<PostImages> postImages = new ArrayList<>();

}
