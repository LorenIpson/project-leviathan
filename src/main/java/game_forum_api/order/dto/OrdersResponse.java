package game_forum_api.order.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import game_forum_api.order.domain.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponse {
    private Integer orderId;
    private Integer memberId;
    private Integer totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private String memberName;
    private List<OrderDetailsResponse> orderDetails;
    
    public OrdersResponse(Orders order) {
        this.orderId = order.getOrderId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.memberId = order.getMember().getId();
        this.orderDetails = order.getOrderDetails().stream()
            .map(OrderDetailsResponse::new)
            .collect(Collectors.toList());
    }

    public static List<OrdersResponse> fromOrdersList(List<Orders> orders) {
        return orders.stream()
            .map(OrdersResponse::new)
            .collect(Collectors.toList());
    }
}
