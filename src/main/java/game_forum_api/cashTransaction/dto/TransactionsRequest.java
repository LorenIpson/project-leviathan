package game_forum_api.cashTransaction.dto;

import jakarta.validation.constraints.Min;
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
public class TransactionsRequest {

    @NotNull(message = "會員 ID 不能為空")
    private Integer memberId;

    @NotNull(message = "交易金額不能為空")
    @Min(value = 1, message = "交易金額必須大於 0")
    private Integer amount;

    @NotBlank(message = "交易類型不能為空")
    private String type;
}
