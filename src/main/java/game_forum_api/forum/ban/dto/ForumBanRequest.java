package game_forum_api.forum.ban.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumBanRequest {

    private Integer banId;
    private String accountId;
    private String reason;
    private LocalDateTime endDate;

}
