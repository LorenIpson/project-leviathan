package game_forum_api.forum.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumRequest {

    private String name;
    private MultipartFile mainCover;
    private MultipartFile secondaryCover;
    private String description;
    private Set<Integer> categoryIds;

    public ForumRequest(MultipartFile mainCover) {
        this.mainCover = mainCover;
    }

}
