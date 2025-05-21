package game_forum_api.ads.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import game_forum_api.ads.model.AdLog;

public interface AdLogRepository extends JpaRepository<AdLog, Long> {
    void deleteByAdId(Long adId);
    
    @Query("SELECT COUNT(a) FROM AdLog a WHERE a.adId = :adId AND a.type = 'view'")
    Long countViewsByAdId(@Param("adId") Long adId);

    @Query("SELECT COUNT(a) FROM AdLog a WHERE a.adId = :adId AND a.type = 'click'")
    Long countClicksByAdId(@Param("adId") Long adId);

    // 可選：若你想一次查所有廣告的統計（建議使用 GROUP BY）
    @Query("SELECT a.adId as adId, " +
           "SUM(CASE WHEN a.type = 'view' THEN 1 ELSE 0 END) as views, " +
           "SUM(CASE WHEN a.type = 'click' THEN 1 ELSE 0 END) as clicks " +
           "FROM AdLog a GROUP BY a.adId")
    List<AdViewClickSummary> findViewClickSummaryGroupedByAdId();

    interface AdViewClickSummary {
        Long getAdId();
        Long getViews();
        Long getClicks();
    }

}
