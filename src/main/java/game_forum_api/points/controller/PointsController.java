package game_forum_api.points.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.annotation.MemberId;
import game_forum_api.points.service.PointsService;

@RestController
@RequestMapping("api/points")
public class PointsController {

	@Autowired
	private PointsService pointsService;

	@GetMapping
	public ResponseEntity<Integer> getPointsByMemberId(@MemberId Integer memberId) {

		return ResponseEntity.ok(pointsService.getPointsByMemberId(memberId));
	}

}
