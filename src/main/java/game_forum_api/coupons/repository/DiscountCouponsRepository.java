package game_forum_api.coupons.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.coupons.domain.DiscountCoupons;

@Repository
public interface DiscountCouponsRepository extends JpaRepository<DiscountCoupons, Integer> {
    Optional<DiscountCoupons> findByCode(String code);
    List<DiscountCoupons> findByMemberId(Integer memberId);
    
}
