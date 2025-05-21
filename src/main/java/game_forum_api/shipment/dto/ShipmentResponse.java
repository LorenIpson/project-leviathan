package game_forum_api.shipment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private Integer shipmentId;
    private String trackingNumber;
    private String carrier;
    private LocalDateTime shippedAt;
    private Integer orderId;  // 只回傳 orderId，避免無限遞迴
}
