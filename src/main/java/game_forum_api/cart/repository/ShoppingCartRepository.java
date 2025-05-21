package game_forum_api.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.cart.domain.ShoppingCart;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findByMemberId(Integer memberId); // 取得某個會員的購物車
}
