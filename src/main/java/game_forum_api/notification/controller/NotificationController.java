package game_forum_api.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.notification.domain.Notification;
import game_forum_api.notification.dto.NewNotification;
import game_forum_api.notification.dto.NotificationsResponse;
import game_forum_api.notification.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	// 查詢會員的通知
	@GetMapping
	public ResponseEntity<NotificationsResponse> getNotificationsByMemberId(@MemberId Integer memberId,
			@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {

		NotificationsResponse response = notificationService.getNotificationsByMemberId(memberId, page, size);
		return ResponseEntity.ok(response);
	}

	// 查詢會員未讀的通知
	@GetMapping("/unread")
	public ResponseEntity<List<Notification>> getUnreadNotificationsByMemberId(@MemberId Integer memberId) {

		List<Notification> response = notificationService.getUnreadNotifications(memberId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{notificationId}/read")
	public ResponseEntity<String> markNotificationAsRead(@MemberId Integer memberId,
			@PathVariable Integer notificationId) {

		notificationService.markAsRead(memberId, notificationId);
		return ResponseEntity.ok("修改成功");
	}

	@PutMapping("/mark-all-read")
	public ResponseEntity<String> markAllAsRead(@MemberId Integer memberId) {
		notificationService.markAllAsRead(memberId);
		return ResponseEntity.ok("更新成功");

	}

	// 測試用
	@PostMapping
	public ResponseEntity<String> createNotification(@RequestBody NewNotification newNotification) {

		notificationService.createNotification(newNotification);
		return ResponseEntity.ok("新增成功");
	}

}
