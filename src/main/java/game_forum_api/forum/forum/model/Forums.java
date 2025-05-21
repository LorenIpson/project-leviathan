package game_forum_api.forum.forum.model;

import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.category.model.Categories;
import game_forum_api.forum.flair.model.ForumFlairs;
import game_forum_api.forum.pin.model.ForumPostsPins;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.tag.model.ForumTags;
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
 * 討論區。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forums")
public class Forums {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_id")
    private Integer id;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "cover")
    private byte[] cover;

    @Column(name = "is_active", nullable = false) // Default 1
    private Boolean isActive;

    @Column(name = "is_visible", nullable = false) // Default 1
    private Boolean isVisible;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "popularity_score")
    private Long popularityScore;

    // ===== FORUM =========================

    /**
     * 討論區與討論區細節。
     */
    @OneToOne(mappedBy = "forum", cascade = CascadeType.ALL)
    private ForumDetail forumDetail;

    // ===== CATEGORIES =========================

    /**
     * 討論區與分類。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "categories_forums",
            joinColumns = @JoinColumn(name = "forum_id", referencedColumnName = "forum_id"), // Current Entity
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "category_id")) // Target
    private List<Categories> categories = new ArrayList<>();

    // ===== MEMBER =========================

    /**
     * 討論區與討論區管理員會員。
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "forums_moderators",
            joinColumns = @JoinColumn(name = "forum_id", referencedColumnName = "forum_id"), // Moderators then forums
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"))
    private List<Member> moderators = new ArrayList<>();


//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "forum_fav",
//            joinColumns = @JoinColumn(name = "forum_id", referencedColumnName = "forum_id"),
//            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "id"))
//    private List<Member> forumFavourites = new ArrayList<>();

    /**
     * 討論區與被懲罰會員。
     */
    @OneToMany(mappedBy = "forum") // Entity obj
    private List<ForumsBans> forumsBans = new ArrayList<>();

    // ===== FLAIRS AND TAGS =========================

    @OneToMany(mappedBy = "forum")
    private List<ForumFlairs> forumFlairs = new ArrayList<>();

    @OneToMany(mappedBy = "forum")
    private List<ForumTags> forumTags = new ArrayList<>();

    // ===== PINS =========================

    @OneToMany(mappedBy = "forum")
    private List<ForumPostsPins> forumPostsPins = new ArrayList<>();

    // ===== POSTS =========================

    @OneToMany(mappedBy = "forum")
    private List<Posts> forumPosts = new ArrayList<>();

}
