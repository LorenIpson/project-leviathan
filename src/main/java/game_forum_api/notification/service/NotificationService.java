package game_forum_api.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.notification.domain.Notification;
import game_forum_api.notification.dto.NewNotification;
import game_forum_api.notification.dto.NotificationsResponse;
import game_forum_api.notification.repository.NotificationRepository;
import game_forum_api.notification.repository.NotificationSettingRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationSettingRepository notificationSettingRepository;

	@Autowired
	private MemberRepository memberRepository;

	// WebSocket 推送工具
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// 新增通知(待移除)
	public void createNotification(NewNotification newNotification) {

		Integer memberId = newNotification.getMemberId();
		if (!memberRepository.existsById(memberId)) {
			throw new ResourceNotFoundException("找不到會員");
		}

		boolean isEnabled = notificationSettingRepository.isNotificationEnabled(memberId, newNotification.getType())
				.orElse(true);
		if (!isEnabled) {
			return; // 使用者關閉此類型通知，不存入
		}

		Notification notification = new Notification();
		notification.setMemberId(newNotification.getMemberId());
		notification.setType(newNotification.getType());
		notification.setMessage(newNotification.getMessage());
		notification.setValue(newNotification.getValue());
		notification.setStatus(0); // 未讀

		notificationRepository.save(notification);

		// 透過 WebSocket 即時推送通知
		messagingTemplate.convertAndSend("/topic/notifications/" + memberId, notification);
	}

	public void createNotification(Integer memberId, String type, String message, String value) {

		if (!memberRepository.existsById(memberId)) {
			throw new ResourceNotFoundException("找不到會員");
		}

		boolean isEnabled = notificationSettingRepository.isNotificationEnabled(memberId, type).orElse(true);
		;
		if (!isEnabled) {
			return; // 使用者關閉此類型通知，不存入
		}

		Notification notification = new Notification();
		notification.setMemberId(memberId);
		notification.setType(type);
		notification.setMessage(message);
		notification.setValue(value);
		notification.setStatus(0); // 未讀

		notificationRepository.save(notification);

		// 透過 WebSocket 即時推送通知
		messagingTemplate.convertAndSend("/topic/notifications/" + memberId, notification);
	}

	// 查詢會員的所有通知(分頁模式)
	public NotificationsResponse getNotificationsByMemberId(Integer memberId, Integer page, Integer size) {

		if (!memberRepository.existsById(memberId)) {
			throw new ResourceNotFoundException("找不到會員");
		}

		Pageable pgb = PageRequest.of(page - 1, size, Sort.Direction.DESC, "sentAt");
		Page<Notification> notificationsPage = notificationRepository.findByMemberId(memberId, pgb);
		List<Notification> notifications = notificationsPage.getContent();

		Integer unreadCount = notificationRepository.countByMemberIdAndStatus(memberId, 0); // status: 0 表示未讀
		
		boolean hasMore = notificationsPage.hasNext();

		return new NotificationsResponse(notifications, unreadCount, hasMore);
	}

	public List<Notification> getUnreadNotifications(Integer memberId) {

		if (!memberRepository.existsById(memberId)) {
			throw new ResourceNotFoundException("找不到會員");
		}

		return notificationRepository.findByMemberIdAndStatusOrderBySentAtDesc(memberId, 0);
	}

	// 改為已讀
	public void markAsRead(Integer memberId, Integer notificationId) {

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到通知"));

		if (!notification.getMemberId().equals(memberId)) {
			throw new ForbiddenException("此通知不屬於該會員");
		}

		notification.setStatus(1);
		notificationRepository.save(notification);

		// 透過 WebSocket 即時推送該 ID 的訊息為已讀
		messagingTemplate.convertAndSend("/topic/notifications/read/" + memberId, notificationId);
	}

	@Transactional
	public void markAllAsRead(Integer memberId) {

		int count = notificationRepository.markAllAsReadByMemberId(memberId);

		// 透過 WebSocket 即時推送該 ID 的訊息為已讀
		messagingTemplate.convertAndSend("/topic/notifications/mark-all-read/" + memberId, count);
	}
}
