package game_forum_api.forum.tag.dto;

import game_forum_api.forum.tag.model.ForumTags;

import java.util.List;
import java.util.stream.Collectors;

public class PostTagsMapper {

    // ===== List<ENTITY> TO List<RESPONSE> ========================================

    public static List<PostTagsResponse> toPostTagsResponse(List<ForumTags> forumTags) {
        return forumTags.stream().map(
                forumTag -> new PostTagsResponse(
                        forumTag.getName()
                )).collect(Collectors.toList());
    }

}
