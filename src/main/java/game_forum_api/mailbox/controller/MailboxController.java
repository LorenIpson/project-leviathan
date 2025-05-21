package game_forum_api.mailbox.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.mailbox.service.MailboxService;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    @Autowired
    private MailboxService mailboxService;

    @GetMapping("/inbox")
    public ResponseEntity<Map<String, Object>> getInbox(@MemberId Integer userId) {
        return ResponseEntity.ok(mailboxService.getInbox(userId));
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMail(@MemberId Integer senderId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(mailboxService.sendMail(senderId, request));
    }

    @DeleteMapping("/delete/{mailId}")
    public ResponseEntity<Map<String, String>> deleteMail(@MemberId Integer userId, @PathVariable Integer mailId) {
        return ResponseEntity.ok(mailboxService.deleteMail(userId, mailId));
    }

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getSentMails(@MemberId Integer senderId) {
        return ResponseEntity.ok(mailboxService.getSentMails(senderId));
    }

    @PutMapping("/mark-as-read/{mailId}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Integer mailId) {
        return ResponseEntity.ok(mailboxService.markAsRead(mailId));
    }

    @GetMapping("/view/{mailId}")
    public ResponseEntity<Map<String, Object>> viewMail(
        @PathVariable Integer mailId,
        @RequestHeader("X-Member-Id") Integer userId // 从请求头中获取当前用户的 ID
    ) {
        return ResponseEntity.ok(mailboxService.viewMail(mailId, userId));
    }
}