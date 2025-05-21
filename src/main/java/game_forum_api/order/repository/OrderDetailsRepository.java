package game_forum_api.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.order.domain.OrderDetails;
import game_forum_api.order.domain.Orders;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
    List<OrderDetails> findByOrders(Orders orders);
}

