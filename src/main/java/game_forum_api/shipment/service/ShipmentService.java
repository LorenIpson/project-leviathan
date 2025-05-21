package game_forum_api.shipment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.order.domain.Orders;
import game_forum_api.order.repository.OrdersRepository;
import game_forum_api.shipment.domain.Shipment;
import game_forum_api.shipment.dto.ShipmentRequest;
import game_forum_api.shipment.dto.ShipmentResponse;
import game_forum_api.shipment.repository.ShipmentRepository;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrdersRepository ordersRepository;

    public ShipmentService(ShipmentRepository shipmentRepository, OrdersRepository ordersRepository) {
        this.shipmentRepository = shipmentRepository;
        this.ordersRepository = ordersRepository;
    }

    // 創建運送紀錄
    public ShipmentResponse createShipment(ShipmentRequest request) {
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        Shipment shipment = new Shipment();
        shipment.setOrders(order);
        shipment.setTrackingNumber(request.getTrackingNumber());
        shipment.setCarrier(request.getCarrier());
        shipment.setShippedAt(request.getShippedAt());

        Shipment savedShipment = shipmentRepository.save(shipment);

        return new ShipmentResponse(
                savedShipment.getShipmentId(),
                savedShipment.getTrackingNumber(),
                savedShipment.getCarrier(),
                savedShipment.getShippedAt(),
                savedShipment.getOrders().getOrderId()
        );
    }
    
    // 查詢全部
    public List<ShipmentResponse> getAllShipments() {
        List<Shipment> shipments = shipmentRepository.findAll();

        if (shipments.isEmpty()) {
            throw new ResourceNotFoundException("目前沒有任何運送紀錄");
        }

        return shipments.stream().map(shipment -> new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getTrackingNumber(),
                shipment.getCarrier(),
                shipment.getShippedAt(),
                shipment.getOrders().getOrderId()
        )).toList();
    }
    
 // 根據 memberId 查詢所有 shipment
    public List<ShipmentResponse> getShipmentsByMemberId(Integer memberId) {
        List<Shipment> shipments = shipmentRepository.findByMemberId(memberId);

        if (shipments.isEmpty()) {
            throw new ResourceNotFoundException("找不到該會員的出貨紀錄");
        }

        return shipments.stream().map(shipment -> new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getTrackingNumber(),
                shipment.getCarrier(),
                shipment.getShippedAt(),
                shipment.getOrders().getOrderId()
        )).toList();
    }
    

    // 查詢運送資訊
    public ShipmentResponse getShipmentByOrderId(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        Shipment shipment = shipmentRepository.findByOrders(order)
                .orElseThrow(() -> new ResourceNotFoundException("該訂單尚未出貨"));

        return new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getTrackingNumber(),
                shipment.getCarrier(),
                shipment.getShippedAt(),
                shipment.getOrders().getOrderId()
        );
    }
    
    private String generateTradeNo(Integer orderId) {
        String randomSuffix = String.valueOf(System.currentTimeMillis()).substring(6, 13);
        return "ORDER" + orderId + randomSuffix;
    }
    
    public void shipOrder(Integer orderId, ShipmentRequest request) {
        Orders order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        // 1. 建立 Shipment
        Shipment shipment = new Shipment();
        shipment.setOrders(order);
        shipment.setCarrier(request.getCarrier());
        shipment.setTrackingNumber(request.getTrackingNumber());
        shipment.setShippedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        // 2. 更新訂單狀態為 shipped
        order.setStatus("shipped");

        // 3. 更新 merchant_trade_no
        String newMerchantTradeNo = generateTradeNo(order.getOrderId());
        order.setMerchantTradeNo(newMerchantTradeNo);

        ordersRepository.save(order);
    }


    // 更新運送資訊
    public ShipmentResponse updateShipment(Integer shipmentId, ShipmentRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到運送紀錄"));

        shipment.setTrackingNumber(request.getTrackingNumber());
        shipment.setCarrier(request.getCarrier());
        shipment.setShippedAt(request.getShippedAt());

        Shipment updatedShipment = shipmentRepository.save(shipment);

        return new ShipmentResponse(
                updatedShipment.getShipmentId(),
                updatedShipment.getTrackingNumber(),
                updatedShipment.getCarrier(),
                updatedShipment.getShippedAt(),
                updatedShipment.getOrders().getOrderId()
        );
    }
    
    // 刪除運送資訊
    public void deleteShipment(Integer shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到訂單"));

        shipmentRepository.delete(shipment);
    }
}

