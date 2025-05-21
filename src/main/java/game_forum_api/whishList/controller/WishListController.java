package game_forum_api.whishList.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.whishList.dto.WishListRequest;
import game_forum_api.whishList.dto.WishListResponse;
import game_forum_api.whishList.service.WishListService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    // 新增願望清單
    @PostMapping("/add")
    public ResponseEntity<WishListResponse> addToWishList(@RequestBody @Valid WishListRequest request) {
        WishListResponse response = wishListService.addToWishList(request);
        return ResponseEntity.ok(response);
    }

    // 查詢會員的願望清單
    @GetMapping("/member")
    public ResponseEntity<List<WishListResponse>> getWishListByMember(@MemberId Integer memberId) {
        List<WishListResponse> response = wishListService.getWishListByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    // 刪除願望清單中的商品
    @DeleteMapping("/delete/{wishlistId}")
    public ResponseEntity<String> removeWishListItem(@PathVariable Integer wishlistId) {
        wishListService.removeWishListItem(wishlistId);
        return ResponseEntity.ok("願望清單項目已刪除");
    }
}

