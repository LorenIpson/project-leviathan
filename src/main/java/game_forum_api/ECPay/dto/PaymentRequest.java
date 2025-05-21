package game_forum_api.ECPay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private int amount;
    private String description;
    private String returnUrl;
    private String notifyUrl;

    public PaymentRequest() {}

    public PaymentRequest(int amount, String description, String returnUrl,String notifyUrl) {
        this.amount = amount;
        this.description = description;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
    }
}
