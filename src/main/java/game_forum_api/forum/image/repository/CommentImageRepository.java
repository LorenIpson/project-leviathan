package game_forum_api.forum.image.repository;

import game_forum_api.forum.image.model.CommentImages;
import game_forum_api.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentImageRepository extends JpaRepository<CommentImages, Integer> {
    Page<CommentImages> findByMemberAndIsTemp(Member member, Boolean isTemp, Pageable pageable);

    List<CommentImages> findByComment_Id(Long commentId);

    boolean existsByComment_Id(Long commentId);
}
