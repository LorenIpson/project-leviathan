package game_forum_api.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.merchant.domain.Merchants;
import game_forum_api.notification.dto.NewNotification;
import game_forum_api.notification.service.NotificationService;
import game_forum_api.order.domain.OrderDetails;
import game_forum_api.order.domain.Orders;
import game_forum_api.order.dto.OrderDetailsRequest;
import game_forum_api.order.dto.OrderDetailsResponse;
import game_forum_api.order.dto.OrdersRequest;
import game_forum_api.order.dto.OrdersResponse;
import game_forum_api.order.repository.OrderDetailsRepository;
import game_forum_api.order.repository.OrdersRepository;
import game_forum_api.points.service.PointsService;
import game_forum_api.product.domain.Products;
import game_forum_api.product.repository.ProductsRepository;
import jakarta.transaction.Transactional;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;
    private final ProductsRepository productsRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final NotificationService notificationService;
    private final PointsService pointsService;

    public OrdersService(OrdersRepository ordersRepository, MemberRepository memberRepository,
                         ProductsRepository productsRepository, OrderDetailsRepository orderDetailsRepository,NotificationService notificationService,PointsService pointsService) {
        this.ordersRepository = ordersRepository;
        this.memberRepository = memberRepository;
        this.productsRepository = productsRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.notificationService = notificationService;
        this.pointsService = pointsService;
    }
    
    @Transactional
    public void saveMerchantTradeNo(Integer orderId, String merchantTradeNo) {
        Orders order = ordersRepository.getByOrderId(orderId);
        if (order != null) {
            order.setMerchantTradeNo(merchantTradeNo);
            ordersRepository.save(order);
            System.out.println("已儲存 MerchantTradeNo：" + merchantTradeNo + " 對應 OrderId：" + orderId);
        } else {
            System.out.println("找不到訂單：" + orderId);
        }
    }
    
    public Integer getOrderIdByMerchantTradeNo(String merchantTradeNo) {
        Orders order = ordersRepository.findByMerchantTradeNo(merchantTradeNo);
        return (order != null) ? order.getOrderId() : null;
    }
    
    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        Orders order = ordersRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("訂單不存在: " + orderId));

        if (!"completed".equals(order.getStatus()) && !"canceled".equals(order.getStatus())) {
            order.setStatus(status);
            ordersRepository.save(order);
            System.out.println("訂單狀態已更新：" + orderId + " -> " + status);

            // 加入付款成功時的通知
            if ("pending".equalsIgnoreCase(status)) {
                Member buyer = order.getMember();
                order.getOrderDetails();

                // 建立 NewNotification DTO
                NewNotification notification = new NewNotification();
                notification.setMemberId(buyer.getId());
                notification.setType("order");
                notification.setValue(order.getOrderId().toString());
                notification.setMessage("您的訂單 #" + order.getOrderId() + " 已完成付款");

                // 呼叫原本的 createNotification
                notificationService.createNotification(notification);
                
                // 點數回饋
                int totalAmount = order.getOrderDetails().stream()
                        .mapToInt(detail -> detail.getPrice() * detail.getQuantity())
                        .sum();

                int rewardPoints = totalAmount / 50; // 每 50 元回饋 1 點
                if (rewardPoints > 0) {
                    pointsService.updatePoints(buyer.getId(), rewardPoints, "購物回饋點數");
                    System.out.println("已回饋點數 " + rewardPoints + " 給會員：" + buyer.getId());
                }
            }
            
            // 通知商家
            if ("pending".equalsIgnoreCase(status)) {
            	Merchants seller = order.getOrderDetails().get(0).getProducts().getMerchant();
            	
            	// 建立 NewNotification DTO
            	NewNotification notification = new NewNotification();
            	notification.setMemberId(seller.getMerchantId());
            	notification.setType("order");
            	notification.setValue(order.getOrderId().toString());
            	notification.setMessage("新的訂單 #" + order.getOrderId() + " 已完成付款");
            	
            	// 呼叫原本的 createNotification
            	notificationService.createNotification(notification);
            }
            
            // 通知買家已出貨
            if ("shipped".equalsIgnoreCase(status)) {
            	Member buyer = order.getMember();
            	
            	// 建立 NewNotification DTO
            	NewNotification notification = new NewNotification();
            	notification.setMemberId(buyer.getId());
            	notification.setType("order");
            	notification.setValue(order.getOrderId().toString());
            	notification.setMessage("您的訂單 #" + order.getOrderId() + " 已出貨");
            	
            	// 呼叫原本的 createNotification
            	notificationService.createNotification(notification);
            }

        } else {
            System.out.println("訂單已完成或取消，不再更新：" + orderId);
        }
    }
    
    public void completeOrder(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        order.setStatus("completed");
        ordersRepository.save(order);
    }
    

    // 創建訂單
    @Transactional
    public OrdersResponse createOrder(OrdersRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        Orders order = new Orders();
        order.setMember(member);
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(request.getStatus());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setMerchantTradeNo(UUID.randomUUID().toString().toUpperCase());

        Orders savedOrder = ordersRepository.save(order);

        List<OrderDetails> orderDetailsList = new ArrayList<>();

        for (OrderDetailsRequest detail : request.getOrderDetails()) {
            Products product = productsRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("找不到商品"));

            // 檢查庫存是否足夠
            if (product.getStock() < detail.getQuantity()) {
                throw new IllegalArgumentException("商品庫存不足：" + product.getName());
            }

            // 建立訂單明細
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrders(savedOrder);
            orderDetail.setProducts(product);
            orderDetail.setQuantity(detail.getQuantity());
            orderDetail.setPrice(detail.getPrice());

            orderDetailsList.add(orderDetail);
        }

        orderDetailsRepository.saveAll(orderDetailsList);

        // 扣除點數（如果有使用）
        if (request.getUsedPoints() != null && request.getUsedPoints() > 0) {
            pointsService.updatePoints(
                    member.getId(),
                    -request.getUsedPoints(), // ❗記得是負的
                    "使用於訂單 #" + savedOrder.getOrderId()
            );
        }

        return new OrdersResponse(
                savedOrder.getOrderId(),
                savedOrder.getMember().getId(),
                savedOrder.getTotalPrice(),
                savedOrder.getStatus(),
                savedOrder.getCreatedAt(),
                savedOrder.getMember().getUsername(),
                orderDetailsList.stream().map(detail -> new OrderDetailsResponse(
                        detail.getProducts().getProductId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                )).toList()
        );
    }


    
    // 查詢商家訂單，商家 memberId
    public List<OrdersResponse> getOrdersByMerchantId(Integer memberId) {
        List<Orders> allOrders = ordersRepository.findAll();

        List<Orders> merchantOrders = allOrders.stream()
            .filter(order -> order.getOrderDetails().stream()
                .anyMatch(detail -> detail.getProducts().getMerchant().getMerchantId().equals(memberId)))
            .collect(Collectors.toList());

        if (merchantOrders.isEmpty()) {
            System.out.println("沒有找到該商家的訂單");
            return List.of(); // 空列表
        }

        return merchantOrders.stream()
            .map(order -> new OrdersResponse(
                order.getOrderId(),
                order.getMember().getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getMember().getUsername(),
                order.getOrderDetails().stream()
                    .filter(detail -> detail.getProducts().getMerchant().getMerchantId().equals(memberId)) // 僅顯示商家的商品
                    .map(detail -> new OrderDetailsResponse(
                        detail.getProducts().getProductId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                    )).toList()
            ))
            .toList();
    }

    // 找全部訂單
    public List<OrdersResponse> getAllOrders(){
    	List<Orders> ordersList = ordersRepository.findAll();
    	
    	if(ordersList.isEmpty()) {
    		throw new ResourceNotFoundException("訂單為空");
    	}
    	
        return ordersList.stream().map(order -> new OrdersResponse(
                order.getOrderId(),
                order.getMember().getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getMember().getUsername(), //只回傳會員名稱
                order.getOrderDetails().stream().map(detail -> new OrderDetailsResponse(
                        detail.getProducts().getProductId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                )).toList()
        )).toList();
    }
    
    // 根據 memberID 查詢訂單
    public List<OrdersResponse> getOrdersByMemberId(Integer memberId) {
        System.out.println("查詢會員訂單，會員 ID：" + memberId);

        List<Orders> orders = ordersRepository.findByMemberId(memberId);
        if (orders.isEmpty()) {
            System.out.println("沒有找到該會員的訂單");
            return List.of(); // 返回空列表，讓 Controller 處理 No Content
        }

        return orders.stream()
            .map(order -> new OrdersResponse(
                order.getOrderId(),
                order.getMember().getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getMember().getUsername(),
                order.getOrderDetails().stream()
                    .map(detail -> new OrderDetailsResponse(
                        detail.getProducts().getProductId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                    ))
                    .toList()
            ))
            .toList();
    }

    // 根據 ID 查詢訂單
    public OrdersResponse getOrderById(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        return new OrdersResponse(
                order.getOrderId(),
                order.getMember().getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getMember().getUsername(),
                order.getOrderDetails().stream().map(detail -> new OrderDetailsResponse(
                        detail.getProducts().getProductId(),
                        detail.getProducts().getName(),
                        detail.getQuantity(),
                        detail.getPrice()
                )).toList()
        );
    }
    
    // 更新MerchantTradeNo
    public void updateMerchantTradeNo(Integer orderId, String newMerchantTradeNo) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("找不到訂單：" + orderId));
        order.setMerchantTradeNo(newMerchantTradeNo);
        ordersRepository.save(order);
    }
    
    // 回傳 Orders 實體（適用於內部處理）
    public Orders getOrderEntityById(Integer orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單：" + orderId));
    }
    
    // 刪除訂單
    public void deleteOrder(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        ordersRepository.delete(order);
    }
}

