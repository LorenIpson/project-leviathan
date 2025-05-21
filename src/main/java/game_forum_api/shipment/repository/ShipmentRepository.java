package game_forum_api.shipment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import game_forum_api.order.domain.Orders;
import game_forum_api.shipment.domain.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    Optional<Shipment> findByOrders(Orders orders);
    
    // 根據訂單內的會員 ID 查詢對應的出貨資料
    @Query("SELECT s FROM Shipment s WHERE s.orders.member.id = :memberId")
    List<Shipment> findByMemberId(@Param("memberId") Integer memberId);
    
    boolean existsByTrackingNumber(String trackingNumber);
}

