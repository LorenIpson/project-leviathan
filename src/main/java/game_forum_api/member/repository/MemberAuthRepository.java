package game_forum_api.member.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.member.model.MemberAuth;

public interface MemberAuthRepository extends JpaRepository<MemberAuth, Integer> {

	@Query("SELECT m.password FROM MemberAuth m WHERE m.id = :id")
	Optional<String> findPasswordById(@Param("id") Integer id);
	
	// 登入失敗記錄次數用
	@Modifying
	@Query("UPDATE MemberAuth m SET m.failedAttempts = m.failedAttempts + 1 WHERE m.id = :id")
	void incrementFailedAttempts(@Param("id") Integer id);
	
	// 登入失敗 n 次，設定鎖定時間
	@Modifying
	@Query("UPDATE MemberAuth m SET m.lockTime = :lockTime WHERE m.id = :id")
	void updateLockTime(@Param("id") Integer id, @Param("lockTime") LocalDateTime lockTime);

	// 重置
	@Modifying
	@Query("UPDATE MemberAuth m SET m.failedAttempts = 0, m.lockTime = NULL WHERE m.id = :id")
	void resetFailedAttemptsAndLockTime(@Param("id") Integer id);
	
	@Modifying
	@Query("UPDATE MemberAuth m SET m.lastLogin = :lastLogin WHERE m.id = :id")
	void updateLastLogin(@Param("id") Integer id, @Param("lastLogin") LocalDateTime lastLogin);
}
