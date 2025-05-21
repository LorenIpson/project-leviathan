package game_forum_api.ads.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdResponseDTO {
    private Long id;
    private String imageUrl;
    private String redirectUrl;
    private String position;
    private Integer width;
    private Integer height;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer sortOrder;
    private Long viewCount;
    private Long clickCount;
}