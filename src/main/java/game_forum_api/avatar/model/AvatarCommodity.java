package game_forum_api.avatar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "avatar_commodity")
public class AvatarCommodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "commodity_name", nullable = false, length = 50)
    private String commodityName; // 商品名稱

    @Column(name = "type", nullable = false, length = 20)
    private String type; // 商品類型

    @Column(name = "photo_path", nullable = false, length = 255)
    private String photoPath; // 圖片路徑

    @Column(name = "shelf_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date shelfTime; // 上架時間

    @Column(name = "point", nullable = false)
    private Integer point; // 商品價格
}