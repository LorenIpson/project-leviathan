package game_forum_api.forum.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    private String title;
    private List<Long> tagsIds;
    private String content;
    private Boolean spoiler;

}
