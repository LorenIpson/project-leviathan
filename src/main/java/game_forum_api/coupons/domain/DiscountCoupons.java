package game_forum_api.coupons.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import game_forum_api.member.model.Member;
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
 * discountCoupons為主控方
 * 無設定casecade，因刪除Coupons不會連動到user
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="discount_coupons ")
public class DiscountCoupons {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="coupon_id")
	private Integer couponId;
	
	@Column(name = "code", unique = true, length = 50)
	private String code;
	
	@Column(name="discount_percentage")
	private BigDecimal discountPercentage; 
	
	@Column(name="expiry_date", columnDefinition = "DATE")
	private LocalDateTime expiryDate;
	
	@Column(name = "status", length = 20)
	private String status;
	
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
