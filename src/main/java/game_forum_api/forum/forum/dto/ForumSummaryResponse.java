package game_forum_api.forum.forum.dto;

import game_forum_api.forum.category.dto.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumSummaryResponse {

    private Integer forumId;
    private String name;
    private String cover;
    private Long popularityScore;
    private List<CategoryResponse> categoryIds;
    private Long mostPopularPostId;
    private String mostPopularPostTitle;

}
