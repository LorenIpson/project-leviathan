package game_forum_api.coupons.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCouponsResponse {
    private Integer couponId;
    private String code;
    private BigDecimal discountPercentage;
    private LocalDateTime expiryDate;
    private String status;
    private String memberName; // 只回傳會員名稱
}
