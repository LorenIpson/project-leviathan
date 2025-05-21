package game_forum_api.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 會再做一個request的原因是，update和create是POST和PUT，不能共用request
public class ShoppingCartUpdateRequest {
    @NotNull(message = "數量不能為空")
    @Min(value = 1, message = "數量必須大於 0")
    private Integer quantity;
}

