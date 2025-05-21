package game_forum_api.webSocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用一個簡單的內存消息代理，目標前綴為 /topic
        config.enableSimpleBroker("/topic");
        // 設置應用程序前綴為 /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊一個 STOMP 端點，客戶端將連接到此端點
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // 允許前端地址
                .withSockJS();
    }
}