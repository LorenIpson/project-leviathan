package game_forum_api.forum.favorite.repository;

import game_forum_api.forum.favorite.model.ForumFav;
import game_forum_api.forum.favorite.model.ForumFavPK;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumFavRepository extends JpaRepository<ForumFav, ForumFavPK> {

    boolean existsByMemberAndForum(Member member, Forums forum);

    void deleteByForumAndMember(Forums forum, Member member);

    Page<ForumFav> findAllByMember(Member member, Pageable pageable);

}