package game_forum_api.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductsResponse {
    private Integer productId;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String category;
    private String imageUrl;
    private LocalDateTime createdAt;
    private String merchantName; 
}

