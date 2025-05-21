package game_forum_api.forum.vote.dto;

import game_forum_api.forum.vote.model.PostsVotes;

public class PostsVotesMapper {

    // ===== ENTITY TO RESPONSE ========================================

    public static PostsVotesResponse toVoteResponseDTO(PostsVotes vote) {
        return new PostsVotesResponse(
                vote.getPost().getId(),
                vote.getVote()
        );
    }

}
