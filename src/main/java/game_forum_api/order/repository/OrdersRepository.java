package game_forum_api.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import game_forum_api.member.model.Member;
import game_forum_api.order.domain.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByMember(Member member);
    List<Orders> findByMemberId(Integer memberId);
    Optional<Orders> findByOrderId(Integer orderId);
    Orders getByOrderId(Integer orderId);
    Orders findByMerchantTradeNo(String merchantTradeNo);
}

