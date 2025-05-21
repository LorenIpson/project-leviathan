package game_forum_api.forum.vote.dto;

import game_forum_api.forum.vote.model.CommentsVotes;

public class CommentsVotesMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static CommentsVotesResponse toVoteResponseDTO(CommentsVotes vote) {
        return new CommentsVotesResponse(
                vote.getComment().getId(),
                vote.getVote()
        );
    }

}
