package game_forum_api.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import game_forum_api.product.domain.ProductReviews;

public interface ProductReviewRepository extends JpaRepository<ProductReviews, Integer> {

    List<ProductReviews> findByProductsProductId(Integer productId);

    Optional<ProductReviews> findByProductsProductIdAndMemberId(Integer productId, Integer memberId);
    
    List<ProductReviews> findByProductsMerchantMerchantId(Integer merchantId);
}
