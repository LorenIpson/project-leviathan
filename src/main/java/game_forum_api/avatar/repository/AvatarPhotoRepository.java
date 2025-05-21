package game_forum_api.avatar.repository;

import game_forum_api.avatar.model.AvatarPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarPhotoRepository extends JpaRepository<AvatarPhoto, Integer> {
    Optional<AvatarPhoto> findByMemberId(Integer memberId); // 返回 Optional<AvatarPhoto>
}