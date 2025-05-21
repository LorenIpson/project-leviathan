package game_forum_api.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.member.dto.MemberLogByPagiationRequest;
import game_forum_api.member.model.MemberLog;
import game_forum_api.member.service.MemberLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/memberLogs")
public class MemberLogController {

	@Autowired
	private MemberLogService memberLogService;

	@PostMapping("/findByPagination")
	public ResponseEntity<Page<MemberLog>> findByIdWithPagination(@MemberId Integer memberId,
			@Valid @RequestBody MemberLogByPagiationRequest request) {

		Page<MemberLog> result = memberLogService.findMemberLogByMemberIdWithPagination(memberId, request);
		return ResponseEntity.ok(result);
	}
}
