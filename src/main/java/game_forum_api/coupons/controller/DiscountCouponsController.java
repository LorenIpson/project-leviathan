package game_forum_api.coupons.controller;

import java.security.Principal;
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

import game_forum_api.annotation.MemberId;
import game_forum_api.coupons.dto.DiscountCouponsRequest;
import game_forum_api.coupons.dto.DiscountCouponsResponse;
import game_forum_api.coupons.service.DiscountCouponsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
public class DiscountCouponsController {

    private final DiscountCouponsService discountCouponsService;

    public DiscountCouponsController(DiscountCouponsService discountCouponsService) {
        this.discountCouponsService = discountCouponsService;
    }

    // 新增折扣券
    @PostMapping("/create")
    public ResponseEntity<DiscountCouponsResponse> createDiscountCoupon(@RequestBody @Valid DiscountCouponsRequest request) {
        DiscountCouponsResponse response = discountCouponsService.createDiscountCoupon(request);
        return ResponseEntity.ok(response);
    }
    
    
    // 取得會員的折價券清單
    @GetMapping("/member")
    public ResponseEntity<List<DiscountCouponsResponse>> getCouponByMemberId(@MemberId Integer memberId) {
        List<DiscountCouponsResponse> couponItems = discountCouponsService.getCouponByMemberId(memberId);
        return ResponseEntity.ok(couponItems);
    }

    // 查詢折扣券
    @GetMapping("/{couponId}")
    public ResponseEntity<DiscountCouponsResponse> getDiscountCoupon(@PathVariable Integer couponId) {
        DiscountCouponsResponse response = discountCouponsService.getDiscountCouponById(couponId);
        return ResponseEntity.ok(response);
    }

    // 更新折扣券
    @PutMapping("/{couponId}/use")
    public ResponseEntity<DiscountCouponsResponse> useCoupon(@PathVariable Integer couponId, Principal principal) {
        DiscountCouponsResponse response = discountCouponsService.markAsUsed(couponId, principal.getName());
        return ResponseEntity.ok(response);
    }

    // 刪除折扣券
    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<Void> deleteDiscountCoupon(@PathVariable Integer couponId) {
        discountCouponsService.deleteDiscountCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}

