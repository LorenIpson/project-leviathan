package game_forum_api.forum.tag.dto;

import game_forum_api.forum.forum.dto.ForumMapper;
import game_forum_api.forum.tag.model.ForumTags;

import java.util.List;
import java.util.stream.Collectors;

public class ForumTagsMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static ForumTagsResponse toForumResponseDTO(ForumTags forumTag) {
        return new ForumTagsResponse(
                forumTag.getId(),
                ForumMapper.toForumResponseDTO(forumTag.getForum()).getForumId(),
                ForumMapper.toForumResponseDTO(forumTag.getForum()).getName(),
                forumTag.getName(),
                forumTag.getColor(),
                forumTag.getIsActive());
    }

    // ===== List<ENTITY> TO List<RESPONSE> ========================================

    public static List<ForumTagsResponse> toForumTagsListResponseDTO(List<ForumTags> forumTags) {
        return forumTags.stream().map(forumTag -> new ForumTagsResponse(
                forumTag.getId(),
                ForumMapper.toForumResponseDTO(forumTag.getForum()).getForumId(),
                ForumMapper.toForumResponseDTO(forumTag.getForum()).getName(),
                forumTag.getName(),
                forumTag.getColor(),
                forumTag.getIsActive()
        )).collect(Collectors.toList());
    }

}
