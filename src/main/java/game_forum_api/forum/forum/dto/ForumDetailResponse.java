package game_forum_api.forum.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumDetailResponse {

    private Integer forumId;
    private String name;
    private String cover;
    private String description;

}
