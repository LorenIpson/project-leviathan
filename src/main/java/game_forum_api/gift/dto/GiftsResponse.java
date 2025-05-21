package game_forum_api.gift.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiftsResponse {
    private Integer giftId;
    private Integer itemId;
    private String message;
    private LocalDateTime sentAt;
    private String senderName;
    private String receiverName;
}
