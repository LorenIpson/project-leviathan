package game_forum_api.ads.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.ads.model.Ad;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByPositionAndIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(
        String position, LocalDateTime now1, LocalDateTime now2);
    
    List<Ad> findByPositionAndIsActiveTrueOrderBySortOrderAsc(String position);

    @Query("SELECT MAX(a.sortOrder) FROM Ad a WHERE a.position = :position")
    Integer findMaxSortOrderByPosition(@Param("position") String position);
    
    @Query("SELECT COALESCE(MAX(a.sortOrder), 0) FROM Ad a")
    int getMaxSortOrder();

}