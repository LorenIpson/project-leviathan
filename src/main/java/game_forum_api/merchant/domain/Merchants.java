package game_forum_api.merchant.domain;

import java.time.LocalDateTime;

import game_forum_api.member.model.Member;
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
 * 因一個merchant只能對應一個user，故user不用設定OneToOne
 * 
 * 一個 Merchant 會有多筆 Transaction
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="merchants")
public class Merchants {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="merchant_id")
	private Integer merchantId;
	
	@OneToOne
	@JoinColumn(name="member_id", nullable = false)
	private Member member;
	
	@Column(name="business_name")
	private String businessName;
	
	@Column(name="business_address")
	private String businessAddress;
	
	@Column(name="business_phone")
	private String businessPhone;
	
	@Column(name="payment_info")
	private String paymentInfo;
	
	@Column(name="created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
}
