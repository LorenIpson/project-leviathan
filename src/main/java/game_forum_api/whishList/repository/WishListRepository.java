package game_forum_api.whishList.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.member.model.Member;
import game_forum_api.whishList.domain.WishList;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findByMember(Member member);
}

