package game_forum_api.points.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import game_forum_api.points.model.Points;

public interface PointsRepository extends JpaRepository<Points, Integer> {

	Optional<Points> findByMemberId(Integer memberId);

}
