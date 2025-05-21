package game_forum_api.gift.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.gift.dto.GiftsRequest;
import game_forum_api.gift.dto.GiftsResponse;
import game_forum_api.gift.service.GiftsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gifts")
public class GiftsController {

    private final GiftsService giftsService;

    public GiftsController(GiftsService giftsService) {
        this.giftsService = giftsService;
    }

    // 創建禮物
    @PostMapping("/create")
    public ResponseEntity<GiftsResponse> createGift(@RequestBody @Valid GiftsRequest request) {
        GiftsResponse response = giftsService.createGift(request);
        return ResponseEntity.ok(response);
    }
    
    // 找全部
    @GetMapping("/getall")
    public ResponseEntity<List<GiftsResponse>> getAllGift() {
        List<GiftsResponse> allGifts = giftsService.getAllGifts();
        return ResponseEntity.ok(allGifts);
    }
    

    // 根據 ID 查詢禮物
    @GetMapping("/{giftId}")
    public ResponseEntity<GiftsResponse> getGift(@PathVariable Integer giftId) {
        GiftsResponse response = giftsService.getGiftById(giftId);
        return ResponseEntity.ok(response);
    }

    // 根據發送者查詢禮物
    @GetMapping("/sent/{senderId}")
    public ResponseEntity<List<GiftsResponse>> getGiftsBySender(@PathVariable Integer senderId) {
        List<GiftsResponse> response = giftsService.getGiftsBySender(senderId);
        return ResponseEntity.ok(response);
    }

    // 更新禮物訊息
    @PutMapping("/update/{giftId}")
    public ResponseEntity<GiftsResponse> updateGift(
            @PathVariable Integer giftId,
            @RequestBody @Valid GiftsRequest request) {
        GiftsResponse response = giftsService.updateGift(giftId, request);
        return ResponseEntity.ok(response);
    }

    // 刪除禮物
    @DeleteMapping("/delete/{giftId}")
    public ResponseEntity<Void> deleteGift(@PathVariable Integer giftId) {
        giftsService.deleteGift(giftId);
        return ResponseEntity.noContent().build();
    }
}

