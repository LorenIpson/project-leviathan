package game_forum_api.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRequest {
    private Integer memberId;
    private String businessName;
    private String businessAddress;
    private String businessPhone;
    private String paymentInfo;
}

