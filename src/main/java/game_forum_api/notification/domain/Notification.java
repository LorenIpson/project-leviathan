package game_forum_api.notification.domain;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;

	@Column(name = "member_id", nullable = false)
	private Integer memberId;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "status", nullable = false)
	private Integer status;

	@Column(name = "value")
	private String value;

	@Column(name = "sent_at", nullable = false)
	private LocalDateTime sentAt = LocalDateTime.now();
}
