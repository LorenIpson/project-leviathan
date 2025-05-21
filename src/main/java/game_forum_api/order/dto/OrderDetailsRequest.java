package game_forum_api.order.dto;

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
public class OrderDetailsRequest {
    
    @NotNull(message = "產品 ID 不能為空")
    private Integer productId;

    @NotNull(message = "數量不能為空")
    @Min(value = 1, message = "數量必須大於 0")
    private Integer quantity;

    @NotNull(message = "價格不能為空")
    private Integer price;
}
