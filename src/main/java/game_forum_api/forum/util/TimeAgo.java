package game_forum_api.forum.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeAgo {

    public static String toTimeAgo(LocalDateTime postTime) {

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(postTime, now);

        long seconds = duration.toSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        long months = days / 30;
        long years = days / 365;

        if (seconds < 60) {
            return "1 分內";
        } else if (minutes < 60) {
            return minutes + " 分前";
        } else if (hours < 24) {
            return hours + " 小時";
        } else if (days < 30) {
            return days + " 天前";
        } else if (months < 12) {
            return months + " 個月前";
        } else {
            return years + " 年前";
        }

    }

}
