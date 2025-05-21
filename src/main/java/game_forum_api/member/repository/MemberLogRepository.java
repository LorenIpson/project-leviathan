package game_forum_api.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import game_forum_api.member.model.MemberLog;

public interface MemberLogRepository extends JpaRepository<MemberLog, Integer> {

	Page<MemberLog> findByMemberId(Integer memberId, Pageable pgb);
}
