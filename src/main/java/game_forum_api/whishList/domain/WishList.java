package game_forum_api.whishList.domain;

import java.time.LocalDateTime;

import game_forum_api.member.model.Member;
import game_forum_api.product.domain.Products;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 
 * 一個user可收藏多筆product
 * 
 * products、user有設定onetomany，方便後續反向查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="wish_list ")
public class WishList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="wishlist_id")
	private Integer wishlistId;
	
	@Column(name="added_at")
	private LocalDateTime addedAt;
	
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;
}
