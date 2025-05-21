package game_forum_api.product.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.product.domain.ProductReviews;
import game_forum_api.product.domain.Products;
import game_forum_api.product.dto.ProductReviewRequest;
import game_forum_api.product.dto.ProductReviewResponse;
import game_forum_api.product.dto.ProductsResponse;
import game_forum_api.product.repository.ProductReviewRepository;
import game_forum_api.product.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepo;
    private final ProductsRepository productsRepo;
    private final MemberRepository memberRepo;
    
    // 取得所有商品評論
    public List<ProductReviewResponse> getAllReviewsForMerchant() {
        List<ProductReviews> reviews = reviewRepo.findAll();

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("完全沒有評論在此!!");
        }

        return reviews.stream().map(review -> new ProductReviewResponse(
            review.getReviewId(),
            review.getRating(),
            review.getReview(),
            review.getMember().getUsername(),   
            review.getCreatedAt(),
            review.getMember().getId(),
            review.getProducts().getName(),
            review.getProducts().getProductId()
        )).toList();
    }
    
    // 取得商家所有商品評論
    public List<ProductReviewResponse> getReviewsByMerchantId(Integer merchantId) {
        List<ProductReviews> reviews = reviewRepo.findByProductsMerchantMerchantId(merchantId);

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("該商家尚無任何評論");
        }

        return reviews.stream().map(review -> new ProductReviewResponse(
            review.getReviewId(),
            review.getRating(),
            review.getReview(),
            review.getMember().getUsername(),
            review.getCreatedAt(),
            review.getMember().getId(),
            review.getProducts().getName(),
            review.getProducts().getProductId()
        )).toList();
    }


    // 查詢商品所有評論
    public List<ProductReviewResponse> getReviewsByProductId(Integer productId) {
        return reviewRepo.findByProductsProductId(productId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 新增評論（每人每商品只能一次）
    public ProductReviewResponse createReview(Integer productId, Integer memberId, ProductReviewRequest dto) {
        if (reviewRepo.findByProductsProductIdAndMemberId(productId, memberId).isPresent()) {
            throw new IllegalStateException("您已評論過此商品");
        }

        Products product = productsRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("商品不存在"));

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("會員不存在"));

        ProductReviews review = new ProductReviews();
        review.setRating(dto.getRating());
        review.setReview(dto.getReview());
        review.setProducts(product);
        review.setMember(member);
        review.setCreatedAt(LocalDateTime.now());

        return toResponseDTO(reviewRepo.save(review));
    }

    // 修改自己的評論
    public ProductReviewResponse updateReview(Integer reviewId, Integer memberId, ProductReviewRequest dto) {
        ProductReviews review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("評論不存在"));

        if (!review.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("無權編輯他人評論");
        }

        review.setRating(dto.getRating());
        review.setReview(dto.getReview());
        return toResponseDTO(reviewRepo.save(review));
    }

    // 刪除自己的評論
    public void deleteReview(Integer reviewId, Integer memberId) {
        ProductReviews review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("評論不存在"));

        if (!review.getMember().getId().equals(memberId)) {
            throw new AccessDeniedException("無權刪除他人評論");
        }

        reviewRepo.delete(review);
    }

    // 將 Entity 轉為 ResponseDTO
    private ProductReviewResponse toResponseDTO(ProductReviews review) {
        ProductReviewResponse dto = new ProductReviewResponse();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setReview(review.getReview());
        dto.setReviewerName(review.getMember().getUsername()); // 依你的 Member 實際欄位調整
        dto.setCreatedAt(review.getCreatedAt());
        dto.setMemberId(review.getMember().getId());
        return dto;
    }
}
