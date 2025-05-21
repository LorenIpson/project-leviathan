package game_forum_api.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import game_forum_api.annotation.MemberId;
import game_forum_api.cart.dto.ShoppingCartRequest;
import game_forum_api.cart.dto.ShoppingCartResponse;
import game_forum_api.cart.dto.ShoppingCartUpdateRequest;
import game_forum_api.cart.service.ShoppingCartService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    // 取得會員的購物車清單
    @GetMapping()
    public ResponseEntity<List<ShoppingCartResponse>> getCartByMemberId(@MemberId Integer memberId) {
        List<ShoppingCartResponse> cartItems = shoppingCartService.getCartByMemberId(memberId);
        return ResponseEntity.ok(cartItems);
    }

    // 新增商品到購物車
    @PostMapping("/add")
    public ResponseEntity<ShoppingCartResponse> addToCart(@RequestBody ShoppingCartRequest request) {
        ShoppingCartResponse response = shoppingCartService.addToCart(request);
        return ResponseEntity.ok(response);
    }

    // 更新購物車商品數量
    @PutMapping("/update/{cartId}")
    public ResponseEntity<ShoppingCartResponse> updateCartItem(
            @PathVariable Integer cartId,
            @RequestBody @Valid ShoppingCartUpdateRequest request) {
    	ShoppingCartResponse updatedCart = shoppingCartService.updateCartItem(cartId, request);
        return ResponseEntity.ok(updatedCart);
    }

    // 刪除購物車商品
    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Integer cartId) {
        shoppingCartService.removeCartItem(cartId);
        return ResponseEntity.ok("商品已從購物車移除");
    }
}
