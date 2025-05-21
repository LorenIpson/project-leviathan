package game_forum_api.order.dto;

import game_forum_api.order.domain.OrderDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer price;
    
    public OrderDetailsResponse(OrderDetails orderDetails) {
        if (orderDetails.getProducts() != null) {
            this.productId = orderDetails.getProducts().getProductId();
            this.productName = orderDetails.getProducts().getName();
        } else {
            this.productId = null;
            this.productName = "未知商品";
        }
        this.quantity = orderDetails.getQuantity();
        this.price = orderDetails.getPrice();
    }
}
