package game_forum_api.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.product.dto.ProductReviewRequest;
import game_forum_api.product.dto.ProductReviewResponse;
import game_forum_api.product.dto.ProductsResponse;
import game_forum_api.product.service.ProductReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;
    
    @GetMapping("/getall")
    public ResponseEntity<List<ProductReviewResponse>> getAllProducts() {
        List<ProductReviewResponse> allProducts = reviewService.getAllReviewsForMerchant();
        return ResponseEntity.ok(allProducts);
    }
    
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ProductReviewResponse>> getReviewsByMerchantName(
    		@PathVariable Integer merchantId) {

        List<ProductReviewResponse> reviews = reviewService.getReviewsByMerchantId(merchantId);
        return ResponseEntity.ok(reviews);
    }

    // 查詢指定商品的所有評論（公開）
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductReviewResponse>> getReviewsByProduct(@PathVariable Integer productId) {
        List<ProductReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // 新增評論（需登入）
    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductReviewResponse> createReview(
            @PathVariable Integer productId,
            @RequestBody ProductReviewRequest dto,
            @MemberId Integer memberId) {
    
        ProductReviewResponse response = reviewService.createReview(productId, memberId, dto);
        return ResponseEntity.ok(response);
    }

    // 更新自己的評論
    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewResponse> updateReview(
            @PathVariable Integer reviewId,
            @RequestBody ProductReviewRequest dto,
            @MemberId Integer memberId) {

        ProductReviewResponse response = reviewService.updateReview(reviewId, memberId, dto);
        return ResponseEntity.ok(response);
    }

    // 刪除自己的評論
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Integer reviewId,
            @MemberId Integer memberId) {

        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok("評論已刪除");
    }

    // 從 UserDetails 中取得會員 ID（根據你的 JWT 設計調整）
    private Integer extractMemberId(UserDetails userDetails) {
        // 假設 userDetails.getUsername() 存的是 memberId
        return Integer.parseInt(userDetails.getUsername());
    }
}

