package game_forum_api.mailbox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "mailbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mailbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "send_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // 新增字段：寄件人的 account_id
    @Transient // 表示該字段不存儲在資料庫中
    private String senderAccountId;
    
    @Transient // 表示該字段不存儲在資料庫中
    private String receiverAccountId; // 新增字段：收件者的 account_id
}