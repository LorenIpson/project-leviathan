package game_forum_api.cashTransaction.domain;

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
 * merchants方有設定onetomany，方便後續查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="transactions ")
public class Transactions {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="transaction_id")
	private Integer transactionId;
	
	@Column(name = "amount", nullable = false)
	private Integer amount;
	
	@Column(name = "type", nullable = false, length = 20)
	private String type;

	@Column(name="transaction_date", insertable = false, updatable = false)
	private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
