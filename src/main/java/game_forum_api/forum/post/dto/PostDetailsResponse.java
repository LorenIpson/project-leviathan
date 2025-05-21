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
public class PostDetailsResponse {

    private Integer forumId;
    private Long postId;
    private Integer memberId;
    private String accountId;
    private String username;

    private byte[] avatar;

    private List<PostTagsResponse> tags;
    private String title;
    private String content;
    private Boolean spoiler;

    private String postTimeAgo;
    private LocalDateTime createdAt;

    private Boolean isEdited;
    private String editTimeAgo;
    private LocalDateTime editedAt;

    private Boolean isRecommended;

    private Long votes;
    private Long views;
    private Long comments;

    private Boolean isLocked;
    private String lockedBy;
    private Boolean isDeleted;
    private String deletedBy;

}
