package game_forum_api.forum.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumTagsResponse {

    private Long id;
    private Integer forumId;
    private String forum;
    private String name;
    private String color;
    private Boolean isActive;

}
