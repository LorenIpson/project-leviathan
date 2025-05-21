package game_forum_api.notification.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationSettingRequest {

	private Map<String, Boolean> settings;

}
