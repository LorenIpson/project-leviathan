package game_forum_api.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import game_forum_api.avatar.dto.AvatarPhotoResponse;
import game_forum_api.avatar.service.AvatarPhotoService;
import game_forum_api.chat.model.ChatMessage;
import game_forum_api.chat.service.ChatService;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AvatarPhotoService avatarPhotoService;

    @Autowired

    private ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage message) {
        try {
            // 獲取發送者的頭像
            AvatarPhotoResponse avatarPhotoResponse = avatarPhotoService.getAvatarPhotoByMemberId(message.getSender());
            if (avatarPhotoResponse != null && avatarPhotoResponse.getFacePhoto() != null) {
                byte[] facePhoto = avatarPhotoResponse.getFacePhoto();
                message.setSenderPhoto(facePhoto);
            }
        } catch (Exception e) {
        	message.setSenderPhoto(null);
        }
        
        chatService.sendMessage(message.getSender(), message.getContent(), message.getGifUrl());
        return message;
    }
}