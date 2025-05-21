package game_forum_api.avatar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.avatar.dto.AvatarPhotoResponse;
import game_forum_api.avatar.dto.SavePhotoRequest;
import game_forum_api.avatar.service.AvatarPhotoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avatar/photo")
public class AvatarPhotoController {

	@Autowired
	private AvatarPhotoService avatarPhotoService;

	@PostMapping("/save")
	public ResponseEntity<String> savePhoto(@MemberId Integer memberId, @Valid @RequestBody SavePhotoRequest request) {
		String result = avatarPhotoService.savePhoto(memberId, request);
		return ResponseEntity.ok(result);
	}
	//取得會員照片
	@GetMapping("/{memberId}")
	public ResponseEntity<AvatarPhotoResponse> getAvatarPhotoByMemberId(@PathVariable Integer memberId) {
	    AvatarPhotoResponse response = avatarPhotoService.getAvatarPhotoByMemberId(memberId);
	    return ResponseEntity.ok(response);
	}
}