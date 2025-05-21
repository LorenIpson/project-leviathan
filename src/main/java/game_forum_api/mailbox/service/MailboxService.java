package game_forum_api.mailbox.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.mailbox.model.Mailbox;
import game_forum_api.mailbox.repository.MailboxRepository;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.notification.service.NotificationService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MailboxService {

	@Autowired
	private MailboxRepository mailboxRepository;

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private NotificationService notificationService;

	private static final int MAIL_LIMIT = 20;

	// 獲取收件匣
	public Map<String, Object> getInbox(Integer userId) {
		// 使用新的方法獲取信件，並按照 sendTime 降序排列
		List<Mailbox> receivedMails = mailboxRepository.findByReceiverIdOrderBySendTimeDesc(userId);
		int mailCount = receivedMails.size();

		// 設置寄件人和收件人的 accountId
		for (Mailbox mail : receivedMails) {
			mail.setSenderAccountId(getMemberAccountId(mail.getSenderId()));
			mail.setReceiverAccountId(getMemberAccountId(mail.getReceiverId()));
		}

		// 信箱容量警示
		String warning = null;
		if (mailCount >= MAIL_LIMIT) {
			warning = "信箱已滿，無法寄信";
		} else if (mailCount >= 15) {
			warning = "信箱即將滿額，請刪除信件";
		}

		// 構建回應
		Map<String, Object> response = new HashMap<>();
		response.put("receivedMails", receivedMails); // 信件列表（新的在前）
		response.put("mailCount", mailCount); // 信件數量
		response.put("mailLimit", MAIL_LIMIT); // 信箱容量限制
		response.put("warning", warning); // 警示訊息
		response.put("status", "success"); // 狀態
		response.put("message", "獲取收件匣成功"); // 訊息
		return response;
	}

	// 發送信件
	public Map<String, String> sendMail(Integer senderId, Map<String, String> request) {
		String receiverAccountId = request.get("receiverAccountId");
		String title = request.get("title");
		String content = request.get("content");

		if (receiverAccountId == null || title == null || content == null) {
			throw new BadRequestException("請求參數不完整");
		}

		Integer receiverId = getMemberIdByAccountId(receiverAccountId);
		Mailbox mail = new Mailbox();
		mail.setSenderId(senderId);
		mail.setReceiverId(receiverId);
		mail.setTitle(title);
		mail.setContent(content);
		mail.setSendTime(new Date());
		Mailbox mailResult = mailboxRepository.save(mail);
		
		String senderAccountId = getMemberAccountId(senderId);
		String notificationMessage = "您收到一封新信件，寄件者： " + senderAccountId;
		notificationService.createNotification(receiverId, "mail", notificationMessage, mailResult.getId().toString());

		return Map.of("status", "success", "message", "信件已發送");
	}

	public Map<String, String> deleteMail(Integer userId, Integer mailId) {
		mailboxpermissions(userId, mailId);
		Mailbox mail = mailboxRepository.findById(mailId).orElseThrow(() -> new ResourceNotFoundException("信件不存在"));

		if (!mail.getReceiverId().equals(userId)) {
			throw new ForbiddenException("無權刪除此信件");
		}

		mail.setIsDeleted(true); // 軟刪除
		mailboxRepository.save(mail);
		return Map.of("status", "success", "message", "信件刪除成功");
	}

	public Map<String, Object> getSentMails(Integer senderId) {
		List<Mailbox> sentMails = mailboxRepository.findBySenderIdOrderBySendTimeDesc(senderId);

		for (Mailbox mail : sentMails) {
			mail.setReceiverAccountId(getMemberAccountId(mail.getReceiverId()));
			mail.setSenderAccountId(getMemberAccountId(mail.getSenderId()));
		}

		Map<String, Object> response = new HashMap<>();
		response.put("sentMails", sentMails);
		response.put("status", "success");
		response.put("message", "獲取已寄信件成功");
		return response;
	}

	public Map<String, String> markAsRead(Integer mailId) {
		Mailbox mail = mailboxRepository.findById(mailId).orElseThrow(() -> new ResourceNotFoundException("信件不存在"));

		mail.setIsRead(true);
		mailboxRepository.save(mail);
		return Map.of("status", "success", "message", "信件已標記為已讀");
	}

	public Map<String, Object> viewMail(Integer mailId, Integer userId) {
	    // 查找邮件并检查是否存在
	    Mailbox mail = mailboxRepository.findById(mailId)
	        .orElseThrow(() -> new ResourceNotFoundException("信件不存在"));

	    // 检查当前用户是否有权限查看该邮件（发送者或接收者）
	    if (!mail.getSenderId().equals(userId) && !mail.getReceiverId().equals(userId)) {
	        throw new ForbiddenException("無權查看此信件");
	    }

	    // 设置发送者和接收者的账户ID
	    mail.setSenderAccountId(getMemberAccountId(mail.getSenderId()));
	    mail.setReceiverAccountId(getMemberAccountId(mail.getReceiverId()));

	    // 构建返回结果
	    Map<String, Object> response = new HashMap<>();
	    response.put("mail", mail);
	    response.put("status", "success");
	    response.put("message", "獲取信件內容成功");
	    return response;
	}

	private String getMemberAccountId(Integer memberId) {
		return memberRepository.findById(memberId).map(Member::getAccountId).orElse(null);
	}

	private Integer getMemberIdByAccountId(String accountId) {
		return memberRepository.findAll().stream().filter(member -> member.getAccountId().equals(accountId)).findFirst()
				.map(Member::getId).orElseThrow(() -> new ResourceNotFoundException("查無此收件人，請確認收件人帳號"));
	}

	public void deleteMailsOlderThan30Days() {
		LocalDateTime cutoffDateTime = LocalDateTime.now(ZoneId.systemDefault()).minusDays(30).withHour(0).withMinute(0)
				.withSecond(0).withNano(0);
		Date cutoffDate = Date.from(cutoffDateTime.atZone(ZoneId.systemDefault()).toInstant());

		try {
			mailboxRepository.deleteMailsOlderThan(cutoffDate);
			System.out.println("已刪除超過 30 天的信件，截止日期：" + cutoffDate);
		} catch (Exception e) {
			System.err.println("刪除信件時發生錯誤：" + e.getMessage());
		}
	}

	private void mailboxpermissions(Integer memberId, Integer mailId) {
		Mailbox mail = mailboxRepository.findById(mailId).orElseThrow(() -> new ResourceNotFoundException("找不到該信件"));
		Integer receiverId = mail.getReceiverId();
		if (receiverId != memberId) {
			throw new ForbiddenException("沒有權限刪");
		}
	
	}
}