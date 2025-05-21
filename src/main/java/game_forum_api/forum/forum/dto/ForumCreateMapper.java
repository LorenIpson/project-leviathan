package game_forum_api.forum.forum.dto;

import game_forum_api.forum.forum.model.Forums;

public class ForumCreateMapper {

    public static ForumCreateResponse toForumCreateResponse(Forums forum) {
        return new ForumCreateResponse(
                forum.getId(),
                forum.getName()
        );
    }

}
