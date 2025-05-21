package game_forum_api.avatar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.avatar.dto.AvatarPhotoResponse;
import game_forum_api.avatar.dto.SavePhotoRequest;
import game_forum_api.avatar.model.AvatarPhoto;
import game_forum_api.avatar.repository.AvatarPhotoRepository;
import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;

@Service
public class AvatarPhotoService {

	@Autowired
	private AvatarPhotoRepository avatarPhotoRepository;

	@Autowired
	private MemberRepository memberRepository;

	public String savePhoto(Integer memberId, SavePhotoRequest request) {
		// 保存 AvatarPhoto
		AvatarPhoto photo = avatarPhotoRepository.findByMemberId(memberId).orElseGet(() -> new AvatarPhoto());

		photo.setMemberId(memberId);

		String cleanBodyPhoto = request.getBodyPhoto().replaceFirst("^data:image/\\w+;base64,", "");
		String cleanFacePhoto = request.getFacePhoto().replaceFirst("^data:image/\\w+;base64,", "");
		byte[] bodyPhotoBytes = null;
		byte[] facePhotoBytes = null;

		try {
			bodyPhotoBytes = java.util.Base64.getDecoder().decode(cleanBodyPhoto);
			facePhotoBytes = java.util.Base64.getDecoder().decode(cleanFacePhoto);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("無效的Base64圖片數據");
		}

		// 更新 Avatar Photo 的 圖片
		photo.setBodyPhoto(bodyPhotoBytes);
		photo.setFacePhoto(facePhotoBytes);
		avatarPhotoRepository.save(photo);

		// 更新 Member 的 photo 欄位
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException("未找到該會員"));
		member.setPhoto(facePhotoBytes);
		memberRepository.save(member);

		return "圖片儲存成功";
	}

	public AvatarPhotoResponse getAvatarPhotoByMemberId(Integer memberId) {
		// 查詢 avatar_photo 表
		AvatarPhoto avatarPhoto = avatarPhotoRepository.findByMemberId(memberId)
				.orElseThrow(() -> new ResourceNotFoundException("未找到該會員的紙娃照片"));

		// 查詢 member 表以取得 accountId
		Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException("未找到該會員"));

		// 構建回應
		AvatarPhotoResponse response = new AvatarPhotoResponse();
		response.setId(avatarPhoto.getId());
		response.setAccountId(member.getAccountId());
		response.setBodyPhoto(avatarPhoto.getBodyPhoto());
		response.setFacePhoto(avatarPhoto.getFacePhoto());

		return response;
	}
}