package game_forum_api.forum.moderator.dto;

import game_forum_api.forum.forum.model.Forums;
import game_forum_api.member.model.Member;

public class ForumModMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static ForumModResponse toForumModResponse(Forums forum) {
        return new ForumModResponse(
                forum.getName(),
                forum.getModerators().stream().map(Member::getAccountId).toList()
        );
    }

}
