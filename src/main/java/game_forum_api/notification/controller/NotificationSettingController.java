package game_forum_api.notification.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.notification.dto.NotificationSettingRequest;
import game_forum_api.notification.service.NotificationSettingService;

@RestController
@RequestMapping("/api/notification-settings")
public class NotificationSettingController {

	@Autowired
	private NotificationSettingService notificationSettingService;

	// 查詢會員的通知
	@GetMapping
	public ResponseEntity<Map<String, Boolean>> getNotificationSettings(@MemberId Integer memberId) {

		Map<String, Boolean> response = notificationSettingService.getNotificationSettings(memberId);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<String> saveNotificationSettings(@MemberId Integer memberId,
			@RequestBody NotificationSettingRequest request) {

		notificationSettingService.saveOrUpdate(memberId, request);
		return ResponseEntity.ok("通知設定已更新");
	}
}
