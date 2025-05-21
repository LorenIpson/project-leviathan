package game_forum_api.product.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import game_forum_api.product.domain.Products;
import game_forum_api.product.dto.ProductRequest;
import game_forum_api.product.dto.ProductsResponse;
import game_forum_api.product.service.ProductsService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductsService productsService;

    @GetMapping("/getall")
    public ResponseEntity<List<ProductsResponse>> getAllProducts() {
        List<ProductsResponse> allProducts = productsService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }
    
    // 獲取某商家的所有商品
    @GetMapping("/merchant/{merchantId}")
    public List<ProductsResponse> getMerchantProducts(@PathVariable Integer merchantId) {
        return productsService.getProductsByMerchant(merchantId);
    }

    @GetMapping("/{id}")
    public ProductsResponse getProductById(@PathVariable Integer id) {
        return productsService.getProductById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        Products savedProduct = productsService.createProduct(request);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Products>updateProduct(@PathVariable Integer id, @RequestBody Products product) {
        Products updatedProduct = productsService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productsService.deleteProduct(id);
    }
}