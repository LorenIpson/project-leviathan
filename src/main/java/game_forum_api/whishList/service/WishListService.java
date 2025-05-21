package game_forum_api.whishList.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.product.domain.Products;
import game_forum_api.product.repository.ProductsRepository;
import game_forum_api.whishList.domain.WishList;
import game_forum_api.whishList.dto.WishListRequest;
import game_forum_api.whishList.dto.WishListResponse;
import game_forum_api.whishList.repository.WishListRepository;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;
    private final ProductsRepository productsRepository;

    public WishListService(WishListRepository wishListRepository, MemberRepository memberRepository, ProductsRepository productsRepository) {
        this.wishListRepository = wishListRepository;
        this.memberRepository = memberRepository;
        this.productsRepository = productsRepository;
    }

    // 新增願望清單
    public WishListResponse addToWishList(WishListRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        Products product = productsRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到商品"));

        WishList wishList = new WishList();
        wishList.setMember(member);
        wishList.setProducts(product);
        wishList.setAddedAt(LocalDateTime.now());

        WishList savedWishList = wishListRepository.save(wishList);

        return new WishListResponse(
                savedWishList.getWishlistId(),
                savedWishList.getAddedAt(),
                savedWishList.getMember().getUsername(),
                savedWishList.getProducts().getName()
        );
    }

    // 查詢某會員的願望清單
    public List<WishListResponse> getWishListByMemberId(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        List<WishList> wishLists = wishListRepository.findByMember(member);

        if (wishLists.isEmpty()) {
            throw new ResourceNotFoundException("該會員沒有願望清單");
        }

        return wishLists.stream().map(wish -> new WishListResponse(
                wish.getWishlistId(),
                wish.getAddedAt(),
                wish.getMember().getUsername(),
                wish.getProducts().getName()
        )).toList();
    }

    // 刪除願望清單中的商品
    public void removeWishListItem(Integer wishlistId) {
        if (!wishListRepository.existsById(wishlistId)) {
            throw new ResourceNotFoundException("找不到該願望清單項目");
        }
        wishListRepository.deleteById(wishlistId);
    }
}

