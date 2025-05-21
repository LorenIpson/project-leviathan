package game_forum_api.gift.service;

import java.util.List;

import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.gift.domain.Gifts;
import game_forum_api.gift.dto.GiftsRequest;
import game_forum_api.gift.dto.GiftsResponse;
import game_forum_api.gift.repository.GiftsRepository;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;

@Service
public class GiftsService {

    private final GiftsRepository giftsRepository;
    private final MemberRepository memberRepository;

    public GiftsService(GiftsRepository giftsRepository, MemberRepository memberRepository) {
        this.giftsRepository = giftsRepository;
        this.memberRepository = memberRepository;
    }

    // 創建禮物
    public GiftsResponse createGift(GiftsRequest request) {
        Member sender = memberRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到發送者"));

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到接收者"));

        Gifts gift = new Gifts();
        gift.setSender(sender);
        gift.setReceiver(receiver);
        gift.setItemId(request.getItemId());
        gift.setMessage(request.getMessage());

        Gifts savedGift = giftsRepository.save(gift);

        return new GiftsResponse(
                savedGift.getGiftId(),
                savedGift.getItemId(),
                savedGift.getMessage(),
                savedGift.getSentAt(),
                savedGift.getSender().getUsername(),
                savedGift.getReceiver().getUsername()
        );
    }
    
    // 全都怪我
    public List<GiftsResponse> getAllGifts(){
    	List<Gifts> giftsList = giftsRepository.findAll();
    	
    	if(giftsList.isEmpty()) {
    		throw new ResourceNotFoundException("完全沒有禮物在此!!");
    	}
    	
        return giftsList.stream().map(gift -> new GiftsResponse(
                gift.getGiftId(),
                gift.getItemId(),
                gift.getMessage(),
                gift.getSentAt(),
                gift.getSender().getUsername(),  
                gift.getReceiver().getUsername()
        )).toList();
    }

    // 查詢禮物
    public GiftsResponse getGiftById(Integer giftId) {
        Gifts gift = giftsRepository.findById(giftId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到禮物"));

        return new GiftsResponse(
                gift.getGiftId(),
                gift.getItemId(),
                gift.getMessage(),
                gift.getSentAt(),
                gift.getSender().getUsername(),
                gift.getReceiver().getUsername()
        );
    }

    // 查詢會員發送的禮物
    public List<GiftsResponse> getGiftsBySender(Integer senderId) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到發送者"));

        return giftsRepository.findBySender(sender).stream()
                .map(gift -> new GiftsResponse(
                        gift.getGiftId(),
                        gift.getItemId(),
                        gift.getMessage(),
                        gift.getSentAt(),
                        gift.getSender().getUsername(),
                        gift.getReceiver().getUsername()
                )).toList();
    }

    // 更新禮物訊息
    public GiftsResponse updateGift(Integer giftId, GiftsRequest request) {
        Gifts gift = giftsRepository.findById(giftId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到禮物"));

        gift.setMessage(request.getMessage());

        Gifts updatedGift = giftsRepository.save(gift);

        return new GiftsResponse(
                updatedGift.getGiftId(),
                updatedGift.getItemId(),
                updatedGift.getMessage(),
                updatedGift.getSentAt(),
                updatedGift.getSender().getUsername(),
                updatedGift.getReceiver().getUsername()
        );
    }

    // 刪除禮物
    public void deleteGift(Integer giftId) {
        Gifts gift = giftsRepository.findById(giftId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到禮物"));

        giftsRepository.delete(gift);
    }
}
