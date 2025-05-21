package game_forum_api.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import game_forum_api.notification.domain.NotificationSetting;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer> {

	@Query("SELECT ns.enabled FROM NotificationSetting ns WHERE ns.memberId = :memberId AND ns.type = :type")
	Optional<Boolean> isNotificationEnabled(@Param("memberId") Integer memberId, @Param("type") String type);

	List<NotificationSetting> findByMemberId(Integer memberId);

	Optional<NotificationSetting> findByMemberIdAndType(Integer memberId, String type);
}