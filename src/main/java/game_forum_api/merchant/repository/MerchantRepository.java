package game_forum_api.merchant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.merchant.domain.Merchants;

@Repository
public interface MerchantRepository extends JpaRepository<Merchants, Integer>  {

    Optional<Merchants> findByMemberId(Integer memberId);
}
