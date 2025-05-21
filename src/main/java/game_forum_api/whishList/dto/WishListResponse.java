package game_forum_api.whishList.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishListResponse {
    private Integer wishlistId;
    private LocalDateTime addedAt;
    private String memberName;  // 只回傳會員名稱
    private String productName; // 只回傳商品名稱
}
