package game_forum_api.notification.dto;

import java.util.List;

import game_forum_api.notification.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponse {

	private List<Notification> notifications;
	
	private Integer unreadCount;
	
	private Boolean hasMore;
}
