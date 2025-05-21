package game_forum_api.points.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import game_forum_api.points.model.PointsLog;

public interface PointsLogRepository extends JpaRepository<PointsLog, Integer> {

	@Query("SELECT p FROM PointsLog p WHERE (p.createdAt >= :startDate OR :startDate IS NULL) "
			+ "AND (p.createdAt <= :endDate OR :endDate IS NULL) " + "AND p.memberId = :memberId "
			+ "ORDER BY p.createdAt DESC")
	Page<PointsLog> findByCreatedAtBetween(Integer memberId, Date startDate, Date endDate, Pageable pageable);
}
