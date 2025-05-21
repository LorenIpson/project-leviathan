package game_forum_api.avatar.controller;

import game_forum_api.avatar.dto.AvatarCommodityRequest;
import game_forum_api.avatar.model.AvatarCommodity;
import game_forum_api.avatar.service.AvatarCommodityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avatar/commodity")
public class AvatarCommodityController {

    @Autowired
    private AvatarCommodityService avatarCommodityService;
    //商品上架
    @PostMapping
    public ResponseEntity<AvatarCommodity> uploadCommodity(@Valid @RequestBody AvatarCommodityRequest request) {
        AvatarCommodity commodity = avatarCommodityService.uploadCommodity(request);
        return ResponseEntity.ok(commodity);
    }
    //商品下架
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommodity(@PathVariable Integer id) {
        String result = avatarCommodityService.deleteCommodity(id);
        return ResponseEntity.ok(result);
    }

    //取得全部商品
    @GetMapping
    public ResponseEntity<List<AvatarCommodity>> getAllCommodities() {
        List<AvatarCommodity> commodities = avatarCommodityService.getAllCommodities();
        return ResponseEntity.ok(commodities);
    }
}