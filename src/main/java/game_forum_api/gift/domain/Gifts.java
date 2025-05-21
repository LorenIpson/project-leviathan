package game_forum_api.gift.domain;

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
 * 一個user可送多份禮物
 * user方有設定onetomany，方便後續查詢
 * 
 * */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="gifts")
public class Gifts {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="gift_id")
	private Integer giftId;
	
	@Column(name="item_id")
	private Integer itemId;
	
	@Column(name = "message", length = 255)
	private String message;
	
	@Column(name="sent_at", insertable = false, updatable = false)
	private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;
}
