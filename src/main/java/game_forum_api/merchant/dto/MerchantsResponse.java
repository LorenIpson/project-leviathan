package game_forum_api.merchant.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantsResponse {
    private Integer merchantId;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private String paymentInfo;
    private LocalDateTime createdAt;
    private String memberName;  
}

