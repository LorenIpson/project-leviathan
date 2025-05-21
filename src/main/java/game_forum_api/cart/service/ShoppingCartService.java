package game_forum_api.cart.service;

import java.util.List;

import org.springframework.stereotype.Service;

import game_forum_api.cart.domain.ShoppingCart;
import game_forum_api.cart.dto.ShoppingCartRequest;
import game_forum_api.cart.dto.ShoppingCartResponse;
import game_forum_api.cart.dto.ShoppingCartUpdateRequest;
import game_forum_api.cart.repository.ShoppingCartRepository;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.product.domain.Products;
import game_forum_api.product.repository.ProductsRepository;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final MemberRepository memberRepository;
    private final ProductsRepository productsRepository;

    // DI
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository,
                               MemberRepository memberRepository,
                               ProductsRepository productsRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.memberRepository = memberRepository;
        this.productsRepository = productsRepository;
    }

    // 取得特定會員的購物車清單
    public List<ShoppingCartResponse> getCartByMemberId(Integer memberId) {
        List<ShoppingCart> carts = shoppingCartRepository.findByMemberId(memberId);
        
        return carts.stream().map(cart -> 
        new ShoppingCartResponse(
            cart.getCartId(),
            cart.getQuantity(),
            cart.getMember().getUsername(),  // 只傳 member 名稱
            cart.getProducts().getName(),  // 只傳 product 名稱
            cart.getAddedAt()
        )).toList();
    }

    // 新增商品到購物車
    public ShoppingCartResponse  addToCart(ShoppingCartRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        Products product = productsRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品"));

        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setMember(member);
        cartItem.setProducts(product);
        cartItem.setQuantity(request.getQuantity());

        ShoppingCart savedCart = shoppingCartRepository.save(cartItem);
        
        return new ShoppingCartResponse(
                savedCart.getCartId(),
                savedCart.getQuantity(),
                savedCart.getMember().getUsername(),  // 只回傳 Member 的名字
                savedCart.getProducts().getName(), // 只回傳 Product 的名稱
                savedCart.getAddedAt()
        );
    }

    // 更新購物車數量
    public ShoppingCartResponse updateCartItem(Integer cartId, ShoppingCartUpdateRequest request) {
        ShoppingCart cartItem = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("購物車商品不存在"));

        cartItem.setQuantity(request.getQuantity());
        ShoppingCart updatedCart = shoppingCartRepository.save(cartItem);
        
        return new ShoppingCartResponse(
                updatedCart.getCartId(),
                updatedCart.getQuantity(),
                updatedCart.getMember().getUsername(),
                updatedCart.getProducts().getName(),
                updatedCart.getAddedAt()
        );
    }

    // 刪除購物車商品
    public void removeCartItem(Integer cartId) {
        if (!shoppingCartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("購物車商品不存在");
        }
        shoppingCartRepository.deleteById(cartId);
    }
}
