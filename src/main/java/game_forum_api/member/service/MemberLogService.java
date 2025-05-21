package game_forum_api.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import game_forum_api.jwt.JwtService;
import game_forum_api.member.dto.MemberLogByPagiationRequest;
import game_forum_api.member.model.MemberLog;
import game_forum_api.member.repository.MemberLogRepository;

@Service
public class MemberLogService {

	@Autowired
	private MemberLogRepository memberLogRepository;

	@Transactional
	public MemberLog createMemberLog(MemberLog memberLog) {
		return memberLogRepository.save(memberLog);
	}

	public Page<MemberLog> findMemberLogByMemberIdWithPagination(Integer memberId, MemberLogByPagiationRequest request) {

		Integer pageNumber = request.getPageNumber();
		Integer pageRow = request.getPageRow();
		Pageable pgb = PageRequest.of(pageNumber - 1, pageRow, Sort.Direction.DESC, "actionTime");
		Page<MemberLog> page = memberLogRepository.findByMemberId(memberId, pgb);
		return page;
	}
}
