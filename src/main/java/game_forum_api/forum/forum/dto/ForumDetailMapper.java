package game_forum_api.forum.forum.dto;

import game_forum_api.forum.forum.model.ForumDetail;
import game_forum_api.forum.util.ByteToBase64;

public class ForumDetailMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static ForumDetailResponse toDetailResponse(ForumDetail forumDetail) {
        return new ForumDetailResponse(
                forumDetail.getForumId(),
                forumDetail.getForum().getName(),
                forumDetail.getCover() != null ? ByteToBase64.toBase64(forumDetail.getCover()) : null,
                forumDetail.getDescription()
        );
    }

}
