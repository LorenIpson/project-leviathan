package game_forum_api.shipment.dto;

import java.time.LocalDateTime;

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
public class ShipmentRequest {

    @NotNull(message = "訂單 ID 不能為空")
    private Integer orderId;

    @NotBlank(message = "追蹤號碼不能為空")
    private String trackingNumber;

    @NotBlank(message = "物流公司名稱不能為空")
    private String carrier;
    
    private LocalDateTime shippedAt;
}
