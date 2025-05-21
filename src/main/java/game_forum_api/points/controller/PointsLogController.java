package game_forum_api.points.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.points.model.PointsLog;
import game_forum_api.points.service.PointsLogService;

@RestController
@RequestMapping("api/points-log")
public class PointsLogController {

	@Autowired
	private PointsLogService pointsLogService;

	@GetMapping
	public ResponseEntity<Page<PointsLog>> getPointsLogByMemberId(@MemberId Integer memberId,
			@RequestParam(value = "startDate", required = false) String startDateStr,
			@RequestParam(value = "endDate", required = false) String endDateStr,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		return ResponseEntity.ok(pointsLogService.getPointsLogs(memberId, startDateStr, endDateStr, page, size));
	}

}
