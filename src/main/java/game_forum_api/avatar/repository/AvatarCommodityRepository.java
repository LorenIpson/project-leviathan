package game_forum_api.avatar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.avatar.model.AvatarCommodity;

public interface AvatarCommodityRepository extends JpaRepository<AvatarCommodity, Integer> {

	@Query("SELECT a.commodityName FROM AvatarCommodity a WHERE a.id = :id")
	String findCommodityNameById(@Param("id") Integer id);

	@Query("SELECT a.point FROM AvatarCommodity a WHERE a.id = :id")
	Integer findPointById(@Param("id") Integer id);
}