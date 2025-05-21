package game_forum_api.avatar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.avatar.dto.CommodityWithOwnedStatus;
import game_forum_api.avatar.dto.EquipRequest;
import game_forum_api.avatar.dto.EquipmentUpdateRequest;
import game_forum_api.avatar.dto.PurchaseCommoditiesRequest;
import game_forum_api.avatar.dto.PurchaseGiftRequest;
import game_forum_api.avatar.dto.StorehouseItemDTO;
import game_forum_api.avatar.service.AvatarStorehouseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avatar/storehouse")
public class AvatarStorehouseController {

	@Autowired
	private AvatarStorehouseService avatarStorehouseService;

	// 取得使用者倉庫
	@GetMapping
	public ResponseEntity<List<StorehouseItemDTO>> getStorehouseByMemberId(@MemberId Integer memberId) {
		List<StorehouseItemDTO> storehouse = avatarStorehouseService.getStorehouseByMemberId(memberId);
		return ResponseEntity.ok(storehouse);
	}

	// 取得使用者已裝備物品
	@GetMapping("/equipped")
	public ResponseEntity<List<CommodityWithOwnedStatus>> getEquippedItems(@MemberId Integer memberId) {
		List<CommodityWithOwnedStatus> equippedItems = avatarStorehouseService.getEquippedItems(memberId);
		return ResponseEntity.ok(equippedItems);
	}

	@PostMapping("/update-equipment")
	public ResponseEntity<String> updateEquipmentStatus(@MemberId Integer memberId,
			@RequestBody List<EquipmentUpdateRequest> updates) {
		avatarStorehouseService.updateEquipmentStatus(memberId, updates);
		return ResponseEntity.ok("裝備狀態更新成功");
	}

	// 裝備
	@PostMapping("/equip")
	public ResponseEntity<String> equipCommodity(@MemberId Integer memberId, @Valid @RequestBody EquipRequest request) {
		String result = avatarStorehouseService.equipCommodity(memberId, request);
		return ResponseEntity.ok(result);
	}

	// 卸裝
	@PostMapping("/unequip")
	public ResponseEntity<String> unequipCommodity(@MemberId Integer memberId,
			@Valid @RequestBody EquipRequest request) {
		String result = avatarStorehouseService.unequipCommodity(memberId, request);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/detail/{type}")
	public ResponseEntity<List<CommodityWithOwnedStatus>> getCommodityWithEquipStatusByType(@PathVariable String type,
			@MemberId Integer memberId) {
		List<CommodityWithOwnedStatus> result = avatarStorehouseService.getCommodityWithEquipStatusByType(type,
				memberId);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/purchase")
	public ResponseEntity<String> purchaseCommodities(@MemberId Integer memberId,
			@RequestBody List<PurchaseCommoditiesRequest> request) {
		avatarStorehouseService.purchaseCommodities(memberId, request);
		return ResponseEntity.ok("購買成功");
	}

	@PostMapping("/purchase-gift")
	public ResponseEntity<String> purchaseGift(@MemberId Integer memberId, @RequestBody PurchaseGiftRequest request) {
		String response = avatarStorehouseService.purchaseGift(memberId, request);
		return ResponseEntity.ok(response);
	}

}