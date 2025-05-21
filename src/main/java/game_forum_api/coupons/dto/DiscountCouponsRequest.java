package game_forum_api.coupons.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCouponsRequest {
    @NotBlank(message = "折扣碼不能為空")
    private String code;

    @NotNull(message = "折扣比例不能為空")
    @DecimalMin(value = "0.0", message = "折扣比例不能小於 0%")
    @DecimalMax(value = "100.0", message = "折扣比例不能超過 100%")
    private BigDecimal discountPercentage;

    @NotNull(message = "到期日期不能為空")
    private LocalDateTime expiryDate;

    @NotBlank(message = "狀態不能為空")
    private String status;

    @NotNull(message = "會員 ID 不能為空")
    private Integer memberId;
}
