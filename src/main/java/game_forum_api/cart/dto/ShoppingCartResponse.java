package game_forum_api.cart.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 返回給前端，避免無線迴圈
public class ShoppingCartResponse {
    private Integer cartId;
    private Integer quantity;
    private String memberName;  // 只傳遞 member 的名字，避免無限遞迴
    private String productName; // 只傳遞 product 的名稱，避免無限遞迴
    private LocalDateTime addedAt;
}
