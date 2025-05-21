package game_forum_api.order.domain;

import java.time.LocalDateTime;
import java.util.List;

import game_forum_api.member.model.Member;
import game_forum_api.shipment.domain.Shipment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 
 * 一個user可傭有多份order
 * user方有設定onetomany，方便後續查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="orders")
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="order_id")
	private Integer orderId;
	
	@Column(name="total_price")
	private Integer totalPrice;
	
	private String status;
	
	@Column(name="created_at")
	private LocalDateTime createdAt;
	
	@Column(name="updated_at")
	private LocalDateTime updatedAt;
	
    @Column(name = "merchant_trade_no", unique = true) 
    private String merchantTradeNo;
	
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;
	
    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private Shipment shipment;
}
