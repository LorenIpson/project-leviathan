package game_forum_api.shipment.controller;

import java.util.List;

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
import game_forum_api.shipment.dto.ShipmentRequest;
import game_forum_api.shipment.dto.ShipmentResponse;
import game_forum_api.shipment.service.ShipmentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    // 創建運送紀錄
    @PostMapping("/create")
    public ResponseEntity<ShipmentResponse> createShipment(@RequestBody @Valid ShipmentRequest request) {
        ShipmentResponse response = shipmentService.createShipment(request);
        return ResponseEntity.ok(response);
    }
    
    // 查詢全部
    @GetMapping("/getall")
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {
        List<ShipmentResponse> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }
    
    @GetMapping("/member")
    public ResponseEntity<List<ShipmentResponse>> getShipmentsByMemberId(@MemberId Integer memberId) {
        List<ShipmentResponse> shipments = shipmentService.getShipmentsByMemberId(memberId);
        return ResponseEntity.ok(shipments);
    }
    
    // 查詢運送資訊（透過 `orderId`）
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentResponse> getShipmentByOrderId(@PathVariable Integer orderId) {
        ShipmentResponse response = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/ship/{orderId}")
    public ResponseEntity<Void> shipOrder(@PathVariable Integer orderId, @RequestBody ShipmentRequest request) {
        shipmentService.shipOrder(orderId, request);
        return ResponseEntity.ok().build();
    }

    // 更新運送資訊
    @PutMapping("/update/{shipmentId}")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @PathVariable Integer shipmentId,
            @RequestBody @Valid ShipmentRequest request) {
        ShipmentResponse response = shipmentService.updateShipment(shipmentId, request);
        return ResponseEntity.ok(response);
    }
    
    // 刪除運送資訊
    @DeleteMapping("/delete/{shipmentId}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer shipmentId) {
    	shipmentService.deleteShipment(shipmentId);
        return ResponseEntity.noContent().build();
    }
}

