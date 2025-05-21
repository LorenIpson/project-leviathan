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
public class ForumBanResponse {

    private Integer banId;
    private String bannedAccountId;
    private String bannedBy;
    private LocalDateTime bannedAt;
    private String bannedReason;
    private LocalDateTime bannedTil;
    private Boolean isPenalized;

}
