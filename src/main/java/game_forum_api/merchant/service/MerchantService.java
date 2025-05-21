package game_forum_api.merchant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.merchant.domain.Merchants;
import game_forum_api.merchant.dto.MerchantRequest;
import game_forum_api.merchant.dto.MerchantsResponse;
import game_forum_api.merchant.repository.MerchantRepository;

@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantsRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 新增
    public Merchants createMerchant(MerchantRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到此會員，無法進行下一步"));

        Merchants merchant = new Merchants();
        merchant.setMember(member);
        merchant.setBusinessName(request.getBusinessName());
        merchant.setBusinessAddress(request.getBusinessAddress());
        merchant.setBusinessPhone(request.getBusinessPhone());
        merchant.setPaymentInfo(request.getPaymentInfo());

        return merchantsRepository.save(merchant);
    }
    
    // 我要全部
    public List<MerchantsResponse> getAllMerchants(){
    	List<Merchants> merchantsList = merchantsRepository.findAll();
    	if(merchantsList.isEmpty()) {
    		throw new ResourceNotFoundException("完全沒有商家在此!!");
    	}
    	return merchantsList.stream().map(merchant -> new MerchantsResponse(
                merchant.getMerchantId(),
                merchant.getBusinessName(),
                merchant.getBusinessAddress(),
                merchant.getBusinessPhone(),
                merchant.getPaymentInfo(),
                merchant.getCreatedAt(),
                merchant.getMember().getUsername() 
        )).toList();
    }
    
    public Merchants findByMemberId(Integer memberId) {
        return merchantsRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("找不到對應商家"));
    }

    // 找ID
    public Merchants getMerchantById(Integer merchantId) {
        return merchantsRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到這個商家，ID: "+merchantId));
    }

    // 更新
    public Merchants updateMerchant(Integer merchantId, MerchantRequest request) {
        Merchants existingMerchant = merchantsRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到這個商家，ID: "+merchantId+"無法更新!"));

        if (request.getBusinessName() != null) {
            existingMerchant.setBusinessName(request.getBusinessName());
        }
        if (request.getBusinessAddress() != null) {
            existingMerchant.setBusinessAddress(request.getBusinessAddress());
        }
        if (request.getBusinessPhone() != null) {
            existingMerchant.setBusinessPhone(request.getBusinessPhone());
        }
        if (request.getPaymentInfo() != null) {
            existingMerchant.setPaymentInfo(request.getPaymentInfo());
        }

        return merchantsRepository.save(existingMerchant);
    }

    // 不要了
    public void deleteMerchant(Integer merchantId) {
    	Merchants existingMerchant = merchantsRepository.findById(merchantId)
    			.orElseThrow(()->new ResourceNotFoundException("找不到這個商家，ID: "+merchantId+"無法刪除!"));

        merchantsRepository.delete(existingMerchant);
    }
}

