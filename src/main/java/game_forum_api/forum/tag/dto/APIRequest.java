package game_forum_api.forum.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class APIRequest {

    private String model;
    private List<APIMessage> messages;
    private Double temperature;
    private Integer max_tokens;
    private Integer top_p;
    private Integer frequency_penalty;
    private Integer presence_penalty;

}
