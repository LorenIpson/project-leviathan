package game_forum_api.gift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.gift.domain.Gifts;
import game_forum_api.member.model.Member;

@Repository
public interface GiftsRepository extends JpaRepository<Gifts, Integer> {
    List<Gifts> findBySender(Member sender);
    List<Gifts> findByReceiver(Member receiver);
}

