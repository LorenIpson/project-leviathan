package game_forum_api.notification.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import game_forum_api.notification.domain.NotificationSetting;
import game_forum_api.notification.dto.NotificationSettingRequest;
import game_forum_api.notification.repository.NotificationSettingRepository;
import jakarta.transaction.Transactional;

@Service
public class NotificationSettingService {

	@Autowired
	private NotificationSettingRepository notificationSettingRepository;

	public Map<String, Boolean> getNotificationSettings(@RequestParam Integer memberId) {
		List<NotificationSetting> settings = notificationSettingRepository.findByMemberId(memberId);
		Map<String, Boolean> settingMap = settings.stream().collect(Collectors.toMap(NotificationSetting::getType,
				NotificationSetting::getEnabled, (existing, replacement) -> existing));

		// 確保所有類型都有預設值
		List<String> allTypes = List.of("mail", "order", "payment", "coupon", "avatar", "post", "ban", "moderator", "points");
		for (String type : allTypes) {
			settingMap.putIfAbsent(type, true); // 預設開啟通知
		}

		return settingMap;
	}

	@Transactional
	public void saveOrUpdate(Integer memberId, NotificationSettingRequest request) {

		Map<String, Boolean> settings = request.getSettings();
		for (Map.Entry<String, Boolean> entry : settings.entrySet()) {

			Optional<NotificationSetting> existingSetting = notificationSettingRepository
					.findByMemberIdAndType(memberId, entry.getKey());

			if (existingSetting.isPresent()) {
				// 如果資料存在，更新 enabled 值
				NotificationSetting setting = existingSetting.get();
				setting.setEnabled(entry.getValue());
				notificationSettingRepository.save(setting);
			} else {
				// 如果資料不存在，新增一筆設定
				NotificationSetting newSetting = new NotificationSetting();
				newSetting.setMemberId(memberId);
				newSetting.setType(entry.getKey());
				newSetting.setEnabled(entry.getValue());
				notificationSettingRepository.save(newSetting);
			}
		}
	}

}
