package game_forum_api.avatar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.avatar.dto.CommodityWithOwnedStatus;
import game_forum_api.avatar.dto.EquippedItemDTO;
import game_forum_api.avatar.dto.StorehouseItemDTO;
import game_forum_api.avatar.model.AvatarStorehouse;

public interface AvatarStorehouseRepository extends JpaRepository<AvatarStorehouse, Integer> {

	List<AvatarStorehouse> findByMemberId(Integer memberId); // 根據 會員ID 查詢

	Optional<AvatarStorehouse> findByMemberIdAndCommodityId(Integer memberId, Integer commodityId); // 根據 memberId 和
																									// commodityId 查詢

	@Query("SELECT new game_forum_api.avatar.dto.CommodityWithOwnedStatus("
			+ "c.id, c.commodityName, c.type, c.photoPath, c.point, c.shelfTime, s.equipmentStatus) "
			+ "FROM AvatarStorehouse s JOIN AvatarCommodity c ON s.commodityId = c.id "
			+ "WHERE s.memberId = :memberId AND s.equipmentStatus = 1")
	List<CommodityWithOwnedStatus> findEquippedItemsByMemberId(@Param("memberId") Integer memberId);

	@Modifying
	@Query("UPDATE AvatarStorehouse s SET s.equipmentStatus = :status " +
	       "WHERE s.memberId = :memberId AND s.commodityId IN " +
	       "(SELECT c.id FROM AvatarCommodity c WHERE c.type = :type)")
	void updateStatusByType(@Param("memberId") Integer memberId, 
	                       @Param("type") String type,
	                       @Param("status") Integer status);

	@Modifying
	@Query("UPDATE AvatarStorehouse s SET s.equipmentStatus = 1 " +
	       "WHERE s.memberId = :memberId AND s.commodityId = :commodityId")
	void equipSingleItem(@Param("memberId") Integer memberId, 
	                    @Param("commodityId") Integer commodityId);

	@Query("SELECT new game_forum_api.avatar.dto.StorehouseItemDTO("
			+ "s.id, c.id, c.commodityName, c.type, c.photoPath, " + // 添加c.id
			"c.shelfTime, c.point, s.equipmentStatus) "
			+ "FROM AvatarStorehouse s JOIN AvatarCommodity c ON s.commodityId = c.id "
			+ "WHERE s.memberId = :memberId")
	List<StorehouseItemDTO> findStorehouseItemsByMemberId(@Param("memberId") Integer memberId);

	@Query("SELECT new game_forum_api.avatar.dto.CommodityWithOwnedStatus(c.id, c.commodityName, c.type, c.photoPath, c.point, c.shelfTime, COALESCE(s.equipmentStatus, -1)) "
			+ "FROM AvatarStorehouse s "
			+ "RIGHT JOIN AvatarCommodity c ON s.commodityId = c.id AND s.memberId = :memberId "
			+ "WHERE c.type = :type " + "ORDER BY s.equipmentStatus, c.shelfTime DESC")
	List<CommodityWithOwnedStatus> findCommoditiesByMemberIdAndType(@Param("memberId") Integer memberId,
			@Param("type") String type);

}