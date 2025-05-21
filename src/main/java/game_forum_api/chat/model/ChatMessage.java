package game_forum_api.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender", nullable = false)
    private Integer sender;

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Temporal(TemporalType.TIMESTAMP) // 指定時間類型
    @Column(name = "timestamp", nullable = false)
    private Date timestamp;
    
    @Column(name = "gif_url", nullable = true) // 新增欄位，允許為空
    private String gifUrl;

    @Transient
    private String senderAccountId; // 用於顯示發送者的 accountId
    
    @Transient
    private byte[] senderPhoto; // 發送者頭像
}