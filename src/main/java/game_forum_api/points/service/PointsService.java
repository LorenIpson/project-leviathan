package game_forum_api.points.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.points.model.Points;
import game_forum_api.points.model.PointsLog;
import game_forum_api.points.repository.PointsLogRepository;
import game_forum_api.points.repository.PointsRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PointsService {

	@Autowired
	private PointsRepository pointsRepository;

	@Autowired
	private PointsLogRepository pointsLogRepository;

	public void updatePoints(Integer memberId, Integer pointsChange, String reason) {

		// 更新點數
		Points points = pointsRepository.findByMemberId(memberId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到該會員"));
		Integer updatedPoints = points.getPoints() + pointsChange;
		if(updatedPoints < 0) {
			throw new BadRequestException("操作失敗，您的點數不足");
		}
		points.setPoints(updatedPoints);
		pointsRepository.save(points);

		// 記錄點數變動
		PointsLog pointsLog = new PointsLog();
		pointsLog.setMemberId(memberId);
		pointsLog.setPointsChange(pointsChange);
		pointsLog.setReason(reason);
		pointsLogRepository.save(pointsLog);
	}

	public Integer getPointsByMemberId(Integer memberId) {
		Points points = pointsRepository.findByMemberId(memberId)
				.orElseThrow(() -> new ResourceNotFoundException("找不到該會員"));
		return points.getPoints();
	}

}
