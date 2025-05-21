package game_forum_api.notification.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import game_forum_api.notification.domain.Notification;
import jakarta.transaction.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	Page<Notification> findByMemberId(Integer memberId, Pageable pageable);

	Integer countByMemberIdAndStatus(Integer memberId, Integer status);

	List<Notification> findByMemberIdAndStatusOrderBySentAtDesc(Integer memberId, Integer status);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.status = 1 WHERE n.memberId = :memberId AND n.status = 0")
	int markAllAsReadByMemberId(@Param("memberId") Integer memberId);
}
