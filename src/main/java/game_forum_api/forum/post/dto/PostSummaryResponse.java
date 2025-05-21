package game_forum_api.forum.post.dto;

import game_forum_api.forum.tag.dto.PostTagsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {

    private Long postId;
    private String username;
    private List<PostTagsResponse> tags;
    private String title;
    private String content;
    private Boolean spoiler;
    private String imagePreviewURL;

    private String postTimeAgo;
    private LocalDateTime createdAt;

    private Boolean isEdited;
    private String editTimeAgo;
    private LocalDateTime editedAt;

    private Boolean isRecommended;

    private String commentTimeAgo;

    private Long votes;
    private Long views;
    private Long comments;

    private Boolean isLocked;
    private Boolean isDeleted;

}
