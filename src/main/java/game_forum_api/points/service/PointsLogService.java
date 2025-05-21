package game_forum_api.points.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import game_forum_api.points.model.PointsLog;
import game_forum_api.points.repository.PointsLogRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PointsLogService {

	@Autowired
	private PointsLogRepository pointsLogRepository;

	public Page<PointsLog> getPointsLogs(Integer memberId, String startDateStr, String endDateStr, int page, int size) {

		Date startDate = null;
		Date endDate = null;

		if (startDateStr != null && !startDateStr.isEmpty()) {
			// 解析開始日期並設置為當天的 00:00:00
			try {
				startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startDate = calendar.getTime();
		}

		if (endDateStr != null && !endDateStr.isEmpty()) {
			// 解析結束日期並設置為當天的 23:59:59
			try {
				endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			endDate = calendar.getTime();
		}

		PageRequest pageable = PageRequest.of(page - 1, size);
		return pointsLogRepository.findByCreatedAtBetween(memberId, startDate, endDate, pageable);
	}
}
