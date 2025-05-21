package game_forum_api.forum.image.repository;

import game_forum_api.forum.image.model.PostImages;
import game_forum_api.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, Integer> {
    List<PostImages> getPostImagesByMemberAndIsTemp(Member member, Boolean isTemp);

    Page<PostImages> findByMemberAndIsTemp(Member member, Boolean isTemp, Pageable pageable);
}
