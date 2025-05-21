package game_forum_api.forum.category.model;

import game_forum_api.forum.forum.model.Forums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 討論區分類。<br>
 * 扁平結構分類且可以多選，不存在子分類。
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "color", length = 50)
    private String color;

    // ===== FORUMS =========================

    /**
     * 分類與討論區。
     */
    @ManyToMany(mappedBy = "categories")
    private List<Forums> forums = new ArrayList<>();

}
