package game_forum_api.ads.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.ads.dto.AdRequestDTO;
import game_forum_api.ads.dto.AdResponseDTO;
import game_forum_api.ads.dto.AdSortDTO;
import game_forum_api.ads.model.Ad;
import game_forum_api.ads.model.AdLog;
import game_forum_api.ads.repository.AdLogRepository;
import game_forum_api.ads.repository.AdRepository;
import jakarta.transaction.Transactional;

@Service
public class AdService {
    @Autowired 
    private AdRepository adRepo;
    
    @Autowired 
    private AdLogRepository logRepo;
    
    public Ad createAd(AdRequestDTO dto) {
        Ad ad = new Ad();
        ad.setImageUrl(dto.getImageUrl());
        ad.setRedirectUrl(dto.getRedirectUrl());
        ad.setPosition(dto.getPosition());
        ad.setWidth(dto.getWidth());
        ad.setHeight(dto.getHeight());

        // 補上必要欄位
        ad.setStartTime(LocalDateTime.now()); // 預設為現在
        ad.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : LocalDateTime.now().plusMonths(6)); // 預設 6 個月後
        ad.setIsActive(true); // 預設啟用

        // 若 sortOrder 為 null，設定為目前最大值 + 1
        int sortOrder = (dto.getSortOrder() != null) ? dto.getSortOrder() : adRepo.getMaxSortOrder() + 1;
        ad.setSortOrder(sortOrder);

        return adRepo.save(ad);
    }

    public List<Ad> getActiveAds(String position) {
        LocalDateTime now = LocalDateTime.now();
        return adRepo.findByPositionAndIsActiveTrueAndStartTimeBeforeAndEndTimeAfter(position, now, now);
    }

    public void logAdEvent(Long adId, String type, Long userId, String ip) {
        AdLog log = new AdLog();
        log.setAdId(adId);
        log.setType(type);
        log.setUserId(userId);
        log.setIpAddress(ip);
        log.setCreatedAt(LocalDateTime.now());
        logRepo.save(log);
    }
    
    public List<Ad> getAllAds() {
        return adRepo.findAll();
    }

    @Transactional
    public void deleteAd(Long id) {
    	logRepo.deleteByAdId(id); // 先刪關聯的 log
        adRepo.deleteById(id);      // 再刪主廣告
    }
    
    public Ad updateAd(Long id, AdRequestDTO dto) {
        Ad ad = adRepo.findById(id).orElseThrow(() -> new RuntimeException("Ad not found"));
        ad.setImageUrl(dto.getImageUrl());
        ad.setRedirectUrl(dto.getRedirectUrl());
        ad.setPosition(dto.getPosition());
        ad.setWidth(dto.getWidth());
        ad.setHeight(dto.getHeight());
        return adRepo.save(ad);
    }
    
    public void updateSortOrder(List<AdSortDTO> list) {
        List<Ad> updatedAds = new ArrayList<>();

        for (AdSortDTO dto : list) {
            Ad ad = adRepo.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Ad not found"));
            ad.setSortOrder(dto.getSortOrder());
            updatedAds.add(ad);
        }

        adRepo.saveAll(updatedAds);
    }
    
    public Integer getNextSortOrder(String position) {
        Integer maxOrder = adRepo.findMaxSortOrderByPosition(position);
        return maxOrder != null ? maxOrder + 1 : 1;
    }
    
    public List<AdResponseDTO> getAllAdsWithStats() {
        List<Ad> ads = adRepo.findAll();
        return ads.stream().map(ad -> {
            AdResponseDTO dto = new AdResponseDTO();
            dto.setId(ad.getId());
            dto.setImageUrl(ad.getImageUrl());
            dto.setRedirectUrl(ad.getRedirectUrl());
            dto.setPosition(ad.getPosition());
            dto.setWidth(ad.getWidth());
            dto.setHeight(ad.getHeight());
            dto.setStartTime(ad.getStartTime());
            dto.setEndTime(ad.getEndTime());
            dto.setSortOrder(ad.getSortOrder());

            // 加上統計資料
            dto.setViewCount(logRepo.countViewsByAdId(ad.getId()));
            dto.setClickCount(logRepo.countClicksByAdId(ad.getId()));

            return dto;
        }).collect(Collectors.toList());
    }
    
    
}
