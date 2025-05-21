package game_forum_api.avatar.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.avatar.dto.CommodityWithOwnedStatus;
import game_forum_api.avatar.dto.EquipRequest;
import game_forum_api.avatar.dto.EquipmentUpdateRequest;
import game_forum_api.avatar.dto.PurchaseCommoditiesRequest;
import game_forum_api.avatar.dto.PurchaseGiftRequest;
import game_forum_api.avatar.dto.StorehouseItemDTO;
import game_forum_api.avatar.model.AvatarStorehouse;
import game_forum_api.avatar.repository.AvatarStorehouseRepository;
import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.service.MemberService;
import game_forum_api.notification.service.NotificationService;
import game_forum_api.points.service.PointsService;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AvatarStorehouseService {

	@Autowired
	private AvatarStorehouseRepository avatarStorehouseRepository;

	@Autowired
	private AvatarCommodityService avatarCommodityService;

	@Autowired
	private PointsService pointsService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MemberService memberService;

	// 取得使用者擁有的全部商品
	public List<StorehouseItemDTO> getStorehouseByMemberId(Integer memberId) {
		return avatarStorehouseRepository.findStorehouseItemsByMemberId(memberId);
	}

	// 裝備商品
	public String equipCommodity(Integer memberId, EquipRequest request) {
		// 使用 orElseThrow 處理 Optional<AvatarStorehouse>
		AvatarStorehouse storehouse = avatarStorehouseRepository
				.findByMemberIdAndCommodityId(memberId, request.getCommodityId())
				.orElseThrow(() -> new ResourceNotFoundException("商品未找到"));

		storehouse.setEquipmentStatus(1); // 設置為已裝備
		avatarStorehouseRepository.save(storehouse);
		return "裝備成功";
	}

	// 卸下裝備
	public String unequipCommodity(Integer memberId, EquipRequest request) {
		// 使用 orElseThrow 處理 Optional<AvatarStorehouse>
		AvatarStorehouse storehouse = avatarStorehouseRepository
				.findByMemberIdAndCommodityId(memberId, request.getCommodityId())
				.orElseThrow(() -> new ResourceNotFoundException("商品未找到"));

		storehouse.setEquipmentStatus(0); // 設置為未裝備
		avatarStorehouseRepository.save(storehouse);
		return "卸裝成功";
	}

	// 讀取使用者裝備
	public List<CommodityWithOwnedStatus> getEquippedItems(Integer memberId) {
		return avatarStorehouseRepository.findEquippedItemsByMemberId(memberId);
	}

	public void updateEquipmentStatus(Integer memberId, List<EquipmentUpdateRequest> updates) {
		// 按類型分組，確保每種類型只有一個更新
		Map<String, EquipmentUpdateRequest> groupedUpdates = updates.stream().collect(Collectors
				.toMap(EquipmentUpdateRequest::getType, Function.identity(), (existing, replacement) -> replacement // 保留最後一個更新
				));

		// 處理每種類型的更新
		groupedUpdates.values().forEach(update -> {
			// 先卸載該類型所有裝備
			avatarStorehouseRepository.updateStatusByType(memberId, update.getType(), 0 // 0 表示卸載
			);

			// 如果有指定商品，則裝備它
			if (update.getCommodityId() != null) {
				avatarStorehouseRepository.equipSingleItem(memberId, update.getCommodityId());
			}
		});
	}

	// 根據類型查詢所有裝備，並返回使用者持有或非持有狀態
	public List<CommodityWithOwnedStatus> getCommodityWithEquipStatusByType(String type, Integer memberId) {
		return avatarStorehouseRepository.findCommoditiesByMemberIdAndType(memberId, type);
	}

	// 購買裝備
	public void purchaseCommodities(Integer memberId, List<PurchaseCommoditiesRequest> commodityList) {

		checkIsDuplicate(commodityList);

		// 用來記錄已經購買過的裝備
		List<String> ownedCommodityNames = new ArrayList<>();

		Integer totalPoint = 0;

		for (PurchaseCommoditiesRequest commodity : commodityList) {
			Integer commodityId = commodity.getCommodityId();

			Optional<AvatarStorehouse> avatarStorehouse = avatarStorehouseRepository
					.findByMemberIdAndCommodityId(memberId, commodityId);

			if (avatarStorehouse.isPresent()) {
				// 儲存已擁有的商品名稱到 ownedCommodityNames(返回錯誤訊息用)
				String commodityName = avatarCommodityService.getCommodityNameById(commodityId);
				ownedCommodityNames.add(commodityName);
			} else {

				// 計算商品點數
				Integer point = avatarCommodityService.getPointById(commodityId);
				totalPoint += point;

				// 購買商品
				AvatarStorehouse newAvatarStorehouse = new AvatarStorehouse();
				newAvatarStorehouse.setMemberId(memberId);
				newAvatarStorehouse.setCommodityId(commodityId);
				newAvatarStorehouse.setEquipmentStatus(0); // 初始未裝備
				avatarStorehouseRepository.save(newAvatarStorehouse);
			}
		}

		if (!ownedCommodityNames.isEmpty()) {
			String message = "購買異常，您已經擁有以下商品：" + String.join("、", ownedCommodityNames);
			throw new DataConflictException(message);
		}

		// 扣款
		pointsService.updatePoints(memberId, -totalPoint, "購買裝備");
		// 通知
		notificationService.createNotification(memberId, "avatar", "成功購買裝備，來更新您的裝備吧!", null);

	}

	// 檢查要購買的裝備清單是否有重複裝備
	private void checkIsDuplicate(List<PurchaseCommoditiesRequest> request) {

		Set<Integer> commodityIds = new HashSet<>();
		for (PurchaseCommoditiesRequest commodity : request) {
			if (!commodityIds.add(commodity.getCommodityId())) {
				throw new BadRequestException("購買的商品中有商品重複");
			}
		}
	}

	// 購買禮物
	public String purchaseGift(Integer memberId, PurchaseGiftRequest request) {

		Integer commodityId = request.getCommodityId();
		Integer recipientId = request.getRecipientId();

		Member recipient = memberService.findById(recipientId);
		Member member = memberService.findById(memberId);

		Optional<AvatarStorehouse> recipientAvatarStorehouse = avatarStorehouseRepository
				.findByMemberIdAndCommodityId(recipientId, commodityId);

		if (recipientAvatarStorehouse.isPresent()) {
			throw new DataConflictException("贈送失敗，" + recipient.getAccountId() + " 已經擁有此裝備");
		} else {
			Integer point = avatarCommodityService.getPointById(commodityId);

			// 扣點
			pointsService.updatePoints(memberId, -point, "贈送禮物");

			// 給收禮者裝備
			AvatarStorehouse newAvatarStorehouse = new AvatarStorehouse();
			newAvatarStorehouse.setMemberId(recipientId);
			newAvatarStorehouse.setCommodityId(commodityId);
			newAvatarStorehouse.setEquipmentStatus(0); // 初始未裝備
			avatarStorehouseRepository.save(newAvatarStorehouse);

			// 給收禮者通知
			String message = "您收到 " + member.getUsername() + "(" + member.getAccountId() + ") 的禮物 --- "
					+ avatarCommodityService.getCommodityNameById(commodityId) + "，來更新您的裝備吧!";
			notificationService.createNotification(recipientId, "avatar", message, null);

			// 計算剩餘點數
			Integer remainPoints = pointsService.getPointsByMemberId(memberId);

			return "贈送成功，您剩餘 " + remainPoints + " 點數!";
		}
	}

}