package game_forum_api.forum.moderator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumModResponse {

    private String forumName;
    private List<String> moderatorAccountIds;

}
