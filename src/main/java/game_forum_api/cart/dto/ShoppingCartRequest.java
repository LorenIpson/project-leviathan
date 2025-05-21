package game_forum_api.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 傳送給後端
public class ShoppingCartRequest {
    private Integer memberId;
    private Integer productId;
    private Integer quantity;
}
