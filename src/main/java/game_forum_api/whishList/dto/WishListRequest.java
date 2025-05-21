package game_forum_api.whishList.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishListRequest {

    @NotNull(message = "會員 ID 不能為空")
    private Integer memberId;

    @NotNull(message = "產品 ID 不能為空")
    private Integer productId;
}
