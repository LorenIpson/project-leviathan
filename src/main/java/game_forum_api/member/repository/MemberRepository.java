package game_forum_api.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.member.model.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

	Optional<Member> findByAccountId(String accountId);

	List<Member> findByRole(Integer role);

	@Query("SELECT m.role FROM Member m WHERE m.id = :id")
	Optional<Integer> findRoleById(@Param("id") Integer id);

	@Query("SELECT m FROM Member m WHERE (m.username LIKE CONCAT('%', :searchTerm, '%') OR m.accountId LIKE CONCAT('%', :searchTerm, '%')) AND m.role IN (1) ORDER BY m.accountId")
	List<Member> searchRole1ByUsernameOrAccountId(String searchTerm);

	@Query("SELECT m FROM Member m WHERE (m.username LIKE CONCAT('%', :searchTerm, '%') OR m.accountId LIKE CONCAT('%', :searchTerm, '%')) ORDER BY m.accountId")
	List<Member> searchByUsernameOrAccountId(String searchTerm);
}
