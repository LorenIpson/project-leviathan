package game_forum_api.product.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.merchant.domain.Merchants;
import game_forum_api.merchant.service.MerchantService;
import game_forum_api.order.domain.OrderDetails;
import game_forum_api.order.domain.Orders;
import game_forum_api.product.domain.Products;
import game_forum_api.product.dto.ProductRequest;
import game_forum_api.product.dto.ProductsResponse;
import game_forum_api.product.repository.ProductsRepository;


@Service
public class ProductsService {

	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private MerchantService merchantService;
	
    // 新增商品
    public Products createProduct(ProductRequest request) {
        Merchants merchant = merchantService.getMerchantById(request.getMerchantId());

        Products product = new Products();
        product.setMerchant(merchant);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
    	
        return productsRepository.save(product);
    }
    
    // 取得所有商品
    public List<ProductsResponse> getAllProducts() {
    	List<Products> productsList = productsRepository.findAll();
    	
    	if(productsList.isEmpty()) {
    		throw new ResourceNotFoundException("完全沒有商家在此!!");
    	}
    	
    	 return productsList.stream().map(product -> new ProductsResponse(
    	            product.getProductId(),
    	            product.getName(),
    	            product.getDescription(),
    	            product.getPrice(),
    	            product.getStock(),
    	            product.getCategory(),
    	            product.getImageUrl(),
    	            product.getCreatedAt(),
    	            product.getMerchant().getBusinessName()
    	    )).toList();
    }
    
    // 依 ID 取得商品
    public ProductsResponse getProductById(Integer productId) {
        Products product = productsRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("找不到這個商品"));

        ProductsResponse response = new ProductsResponse();
        response.setProductId(product.getProductId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setCreatedAt(product.getCreatedAt());
        response.setMerchantName(product.getMerchant().getBusinessName());

        return response;
    }
    
    public List<ProductsResponse> getProductsByMerchant(Integer merchantId) {
        List<Products> products = productsRepository.findByMerchant_MerchantId(merchantId);
        return products.stream()
                .map(p -> new ProductsResponse(
                        p.getProductId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory(),  
                        p.getImageUrl(),  
                        p.getCreatedAt(), 
                        p.getMerchant().getBusinessName()
                ))
                .toList();
    }
    
    // 更新商品
    public Products updateProduct(Integer productId, Products updatedProduct) {
        Products existingProduct = productsRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到這個商品"));

        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getStock() != null) {
            existingProduct.setStock(updatedProduct.getStock());
        }
        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }
        if (updatedProduct.getMerchant() != null) {
            existingProduct.setMerchant(updatedProduct.getMerchant());
        }
        if (updatedProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
        }

        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productsRepository.save(existingProduct);
    }
    
    // 負責根據訂單扣除商品庫存
    public void updateStockAfterOrder(Orders order) {
        for (OrderDetails detail : order.getOrderDetails()) {
            Products product = detail.getProducts();
            int newStock = product.getStock() - detail.getQuantity();

            if (newStock < 0) {
                System.out.println("商品庫存不足: " + product.getName());
                throw new RuntimeException("商品庫存不足: " + product.getName());
            }

            product.setStock(newStock);
            productsRepository.save(product);
            System.out.println("已扣除庫存：" + product.getName() + "，剩餘庫存：" + newStock);
        }
    }
    
    // 刪除商品
    public void deleteProduct(Integer productId) {
    	Products existingProduct = productsRepository.findById(productId)
    			.orElseThrow(() -> new ResourceNotFoundException("找不到這個商品"));
    	
        productsRepository.delete(existingProduct);
    }
}
