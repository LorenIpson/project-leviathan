package game_forum_api.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import game_forum_api.product.domain.Products;


public interface ProductsRepository extends JpaRepository<Products, Integer> {
    // 查詢指定商家的商品列表
    List<Products> findByMerchant_MerchantId(Integer merchantId);
}
