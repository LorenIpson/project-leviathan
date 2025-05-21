package game_forum_api.order.dto;

import java.util.List;

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
public class OrdersRequest {
    
    @NotNull(message = "會員 ID 不能為空")
    private Integer memberId;

    @NotNull(message = "總價格不能為空")
    private Integer totalPrice;

    @NotBlank(message = "狀態不能為空")
    private String status;

    @NotNull(message = "訂單明細不能為空")
    private List<OrderDetailsRequest> orderDetails;
    
    private Integer couponId;
    
    private Integer usedPoints;
}
