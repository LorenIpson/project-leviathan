package game_forum_api.forum.ban.repository;

import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumsBansRepository extends JpaRepository<ForumsBans, Integer> {
    Optional<ForumsBans> findFirstByForumAndMemberOrderByBanIdDesc(Forums forum, Member member);
}
