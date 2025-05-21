package game_forum_api.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponse {
    private Integer reviewId;
    private BigDecimal rating;
    private String review;
    private String reviewerName;
    private LocalDateTime createdAt;
    private Integer memberId;
    private String productName;
    private Integer productId;
}
