package game_forum_api.product.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private Integer merchantId;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private String category;
    private String imageUrl;

}
