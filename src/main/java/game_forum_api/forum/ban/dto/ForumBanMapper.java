package game_forum_api.forum.ban.dto;

import game_forum_api.forum.ban.model.ForumsBans;

import java.util.List;
import java.util.stream.Collectors;

public class ForumBanMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static ForumBanResponse toForumBanResponse(ForumsBans ban) {
        return new ForumBanResponse(
                ban.getBanId(),
                ban.getMember().getAccountId(),
                ban.getBannedBy().getAccountId(),
                ban.getBannedAt(),
                ban.getBanReason(),
                ban.getBannedTil(),
                ban.getIsPenalized()
        );
    }

    // ===== List<ENTITY> TO List<RESPONSE> ========================================

    public static List<ForumBanResponse> toForumBanResponse(List<ForumsBans> forumsBans) {
        return forumsBans.stream()
                .map(ban -> new ForumBanResponse(
                        ban.getBanId(),
                        ban.getMember().getAccountId(),
                        ban.getBannedBy().getAccountId(),
                        ban.getBannedAt(),
                        ban.getBanReason(),
                        ban.getBannedTil(),
                        ban.getIsPenalized()
                )).collect(Collectors.toList());
    }

}
