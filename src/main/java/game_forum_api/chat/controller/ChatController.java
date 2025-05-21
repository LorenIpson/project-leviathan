package game_forum_api.chat.controller;

import game_forum_api.annotation.MemberId;
import game_forum_api.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // 獲取所有聊天記錄
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages() {
        return ResponseEntity.ok(chatService.getMessages());
    }
}