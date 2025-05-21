package game_forum_api.cart.domain;

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
 * 一個user可有多比shoppingCart紀錄
 * 
 * products、user有設定onetomany，方便後續反向查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="shopping_cart")
public class ShoppingCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cart_id")
	private Integer cartId;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@Column(name="added_at", insertable = false, updatable = false)
	private LocalDateTime addedAt ;
	
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;
}
