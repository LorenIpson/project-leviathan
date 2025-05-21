package game_forum_api.ads.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AdSortDTO {
    private Long id;
    private Integer sortOrder;
}
