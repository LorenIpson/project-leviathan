package game_forum_api.merchant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.merchant.domain.Merchants;
import game_forum_api.merchant.dto.MerchantRequest;
import game_forum_api.merchant.dto.MerchantsResponse;
import game_forum_api.merchant.service.MerchantService;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantsService;

    @GetMapping("/getall")
    public ResponseEntity<List<MerchantsResponse>> getAllMerchants(){
    	List<MerchantsResponse> allMerchants = merchantsService.getAllMerchants();
    	return ResponseEntity.ok(allMerchants);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Merchants> getMerchantById(@PathVariable Integer id) {
    	Merchants merchant = merchantsService.getMerchantById(id);
    	return ResponseEntity.ok(merchant);
    }
    
    @PostMapping("/create")
    public ResponseEntity<Merchants> createMerchant(@RequestBody MerchantRequest request) {
        Merchants savedMerchant = merchantsService.createMerchant(request);
        return ResponseEntity.ok(savedMerchant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Merchants> updateMerchant(@PathVariable Integer id, @RequestBody MerchantRequest request) {
        Merchants updatedMerchant = merchantsService.updateMerchant(id, request);
        return ResponseEntity.ok(updatedMerchant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable Integer id) {
        merchantsService.deleteMerchant(id);
        return ResponseEntity.ok("Merchant deleted successfully");
    }
}

