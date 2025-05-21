package game_forum_api.shipment.domain;

import java.time.LocalDateTime;

import game_forum_api.order.domain.Orders;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 
 * 一個order可對應一個shipment
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="shipment")
public class Shipment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="shipment_id")
	private Integer shipmentId;
	
	@Column(name="tracking_number")
	private String trackingNumber;
	
	@Column(name = "carrier")
	private String carrier;
	
	@Column(name="shipped_at")
	private LocalDateTime shippedAt;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;
}
