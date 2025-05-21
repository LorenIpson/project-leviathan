package game_forum_api.ads.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import game_forum_api.ads.dto.AdRequestDTO;
import game_forum_api.ads.dto.AdResponseDTO;
import game_forum_api.ads.dto.AdSortDTO;
import game_forum_api.ads.model.Ad;
import game_forum_api.ads.service.AdService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    @Autowired
    private AdService adService;
    
    // 前台使用：根據位置取得啟用中的廣告
    @GetMapping
    public List<Ad> getAds(@RequestParam String position) {
        return adService.getActiveAds(position);
    }

    // 管理員使用：取得所有廣告
    @GetMapping("/allads")
    public List<Ad> getAllAds() {
        return adService.getAllAds();
    }
    
 
    @GetMapping("/all")
    public List<AdResponseDTO> getAllAdsWithViewClickStats() {
        return adService.getAllAdsWithStats();
    }
   

    // 新增廣告
    @PostMapping
    public Ad createAd(@RequestBody AdRequestDTO dto) {
        return adService.createAd(dto);
    }

    // 刪除廣告
    @DeleteMapping("/{id}")
    public void deleteAd(@PathVariable Long id) {
        adService.deleteAd(id);
    }

    // 廣告曝光紀錄
    @PostMapping("/view")
    public void viewAd(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long adId = body.get("adId");
        String ip = request.getRemoteAddr();
        adService.logAdEvent(adId, "view", null, ip);
    }
    
    @PutMapping("/{id}")
    public Ad updateAd(@PathVariable Long id, @RequestBody AdRequestDTO dto) {
        return adService.updateAd(id, dto);
    }

    // 廣告點擊紀錄
    @PostMapping("/click")
    public void clickAd(@RequestBody Map<String, Long> body, HttpServletRequest request) {
        Long adId = body.get("adId");
        String ip = request.getRemoteAddr();
        adService.logAdEvent(adId, "click", null, ip);
    }
    
    @PostMapping("/update-order")
    public void updateAdOrder(@RequestBody List<AdSortDTO> list) {
        adService.updateSortOrder(list);
    }
    
    
} 
