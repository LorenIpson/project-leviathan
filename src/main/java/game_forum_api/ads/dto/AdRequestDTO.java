package game_forum_api.ads.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class AdRequestDTO {
	private Long id;
    private String imageUrl;
    private String redirectUrl;
    private String position;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer width;
    private Integer height;
    private Integer sortOrder;
}
