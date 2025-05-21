package game_forum_api.chat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.chat.model.ChatMessage;
import game_forum_api.chat.repository.ChatRepository;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.repository.MemberRepository;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 發送訊息
    public Map<String, String> sendMessage(Integer id, String content, String gifUrl) {
        // 根據 id 獲取 account_id
        String senderAccountId = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該用戶"))
                .getAccountId();

        // 創建並保存訊息
        ChatMessage message = new ChatMessage();
        message.setSender(id); // 保存 sender 的 id
        message.setTimestamp(new Date());

        if (gifUrl != null && !gifUrl.isEmpty()) {
            // 如果是貼圖，存儲 gif_url
            message.setGifUrl(gifUrl);
            message.setContent(null); // 確保 content 為空
        } else {
            // 如果是文字訊息，存儲 content
            message.setContent(content);
            message.setGifUrl(null); // 確保 gif_url 為空
        }

        chatRepository.save(message);

        // 返回成功訊息
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "訊息已發送");
        return response;
    }

    // 獲取所有訊息
    public Map<String, Object> getMessages() {
        List<ChatMessage> messages = chatRepository.findAllByOrderByTimestampAsc(); // 按時間升序排序

        // 設置發送者的 account_id
        for (ChatMessage message : messages) {
            String senderAccountId = memberRepository.findById(message.getSender())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到發送者"))
                    .getAccountId();
            message.setSenderAccountId(senderAccountId); // 設置 account_id
        }

        // 返回訊息列表
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages);
        response.put("status", "success");
        response.put("message", "獲取訊息成功");
        return response;
    }
}