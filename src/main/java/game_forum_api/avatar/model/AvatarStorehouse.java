package game_forum_api.avatar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "avatar_storehouse")
public class AvatarStorehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 會員ID

    @Column(name = "commodity_id", nullable = false)
    private Integer commodityId; // 商品ID

    @Column(name = "equipment_status", nullable = false)
    private Integer equipmentStatus; // 裝備狀態 (0: 未裝備, 1: 已裝備)
}