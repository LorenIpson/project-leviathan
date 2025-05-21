package game_forum_api.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewNotification {

	private Integer memberId;

	private String type;

	private String message;

	private String value;
}
