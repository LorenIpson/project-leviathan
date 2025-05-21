package game_forum_api.gift.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiftsRequest {
    
    @NotNull(message = "贈送者 ID 不能為空")
    private Integer senderId;

    @NotNull(message = "接收者 ID 不能為空")
    private Integer receiverId;

    @NotNull(message = "物品 ID 不能為空")
    private Integer itemId;

    @Size(max = 255, message = "訊息不能超過 255 字")
    private String message;
}