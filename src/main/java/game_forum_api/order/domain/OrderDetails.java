package game_forum_api.order.domain;

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
 * 一個order可傭有多份orderdetail
 * order方有設定onetomany，方便後續查詢
 * 
 * 一個 OrderDetail 只能對應一個 Product
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_details")
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="detail_id")
	private Integer detailId;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@Column(name = "price", nullable = false)
	private Integer price;
	
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;
	
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;
}
