package game_forum_api.product.domain;

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

/* 一個user可對product留下頻論
 * 一個product可有多筆頻論
 * 
 * products、user有設定onetomany，方便後續反向查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="product_reviews")
public class ProductReviews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="review_id")
	private Integer reviewId;
	
	@Column(name = "rating", precision = 2, scale = 1)
	private BigDecimal rating;
	
	@Column(name = "review", columnDefinition = "NVARCHAR(MAX)")
	private String review;
	
	@Column(name="created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products products;
}
