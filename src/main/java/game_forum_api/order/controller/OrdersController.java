package game_forum_api.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.coupons.service.DiscountCouponsService;
import game_forum_api.order.dto.OrdersRequest;
import game_forum_api.order.dto.OrdersResponse;
import game_forum_api.order.service.OrdersService;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;
    private final DiscountCouponsService discountCouponsService;

    public OrdersController(OrdersService ordersService,DiscountCouponsService discountCouponsService) {
        this.ordersService = ordersService;
        this.discountCouponsService = discountCouponsService;
    }

    // 創建訂單
    @PostMapping("/create")
    public ResponseEntity<OrdersResponse> createOrder(@RequestBody OrdersRequest request) {
        if (request.getCouponId() != null && request.getMemberId() != null) {
            discountCouponsService.validateAndUseCoupon(request.getCouponId(), request.getMemberId());
        }

        OrdersResponse response = ordersService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    
    // 查詢所有訂單
    @GetMapping("/getall")
    public ResponseEntity<List<OrdersResponse>> getAllOrders(){
    	List<OrdersResponse> response = ordersService.getAllOrders();
    	return ResponseEntity.ok(response);
    }

    // 查詢訂單
    @GetMapping("/{orderId}")
    public ResponseEntity<OrdersResponse> getOrder(@PathVariable Integer orderId) {
        OrdersResponse response = ordersService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Integer orderId) {
        ordersService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        ordersService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
    
    // memberId查詢訂單
    @GetMapping("/member")
    public ResponseEntity<List<OrdersResponse>> getOrdersByMemberId(@MemberId Integer memberId) {
        List<OrdersResponse> orders = ordersService.getOrdersByMemberId(memberId);

        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        }

        return ResponseEntity.ok(orders); // 200 OK
    }
    
    @GetMapping("/merchant")
    public ResponseEntity<List<OrdersResponse>> getOrdersByMerchantId(@MemberId Integer memberId) {
        List<OrdersResponse> orders = ordersService.getOrdersByMerchantId(memberId);
        return ResponseEntity.ok(orders);
    }
    
    // 刪除訂單
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer orderId) {
    	ordersService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}

