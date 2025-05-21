package game_forum_api.product.domain;

import java.time.LocalDateTime;
import java.util.List;


import game_forum_api.cart.domain.ShoppingCart;
import game_forum_api.merchant.domain.Merchants;
import game_forum_api.order.domain.OrderDetails;
import game_forum_api.whishList.domain.WishList;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchants merchant; // 商家

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stock;

    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "image_url", length = 255, nullable = true)
    private String imageUrl;

	@Column(name="created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(name="updated_at", insertable = false, updatable = true)
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductReviews> reviews;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShoppingCart> cartItems;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishList> wishList;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;
}