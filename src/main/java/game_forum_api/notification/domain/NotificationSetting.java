package game_forum_api.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_setting", uniqueConstraints = { @UniqueConstraint(columnNames = { "member_id", "type" }) })
@Getter
@Setter
@NoArgsConstructor
public class NotificationSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "member_id", nullable = false)
	private Integer memberId;

	@Column(name = "type", nullable = false, length = 20)
	private String type;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled = true;
}
