package game_forum_api.forum.vote.service;

import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.ban.repository.ForumsBansRepository;
import game_forum_api.forum.post.model.Posts;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.vote.dto.PostsVotesRequest;
import game_forum_api.forum.vote.dto.PostsVotesResponse;
import game_forum_api.forum.vote.model.PostsVotes;
import game_forum_api.forum.vote.model.PostsVotesPK;
import game_forum_api.forum.vote.repository.PostsVotesRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PostsVotesService {

    private final PostsVotesRepository postsVotesRepos;

    private final PostsRepository postsRepos;

    private final ForumsBansRepository forumsBansRepos;

    public PostsVotesService(PostsVotesRepository postsVotesRepos,
                             PostsRepository postsRepos,
                             ForumsBansRepository forumsBansRepos) {
        this.postsVotesRepos = postsVotesRepos;
        this.postsRepos = postsRepos;
        this.forumsBansRepos = forumsBansRepos;
    }

    // ===== CREATE ========================================

    @Deprecated
    public String postVote(Member member, Long postId, PostsVotesRequest voteDTO) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        if (targetPost.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetPost.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

        List<Member> forumsBans = targetPost.getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
        boolean isBanned = forumsBans.contains(member);
        if (isBanned) {
            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
        }

        PostsVotesPK newVotePK = new PostsVotesPK(postId, member.getId());
        PostsVotes newVote = new PostsVotes();

        newVote.setPostsVotesPK(newVotePK);
        newVote.setMember(member);
        newVote.setPost(targetPost);
        newVote.setVote(voteDTO.getVote());
        newVote.setVotedAt(LocalDateTime.now());
        postsVotesRepos.save(newVote);

        return "（測試用）評分成功。";

    }

    // ===== RETRIEVE ========================================

    public PostsVotesResponse getMemberPostVote(Member member, Long postId) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        List<PostsVotes> byPostAndMember = postsVotesRepos.findByPostAndMember(targetPost, member);
        if (byPostAndMember.isEmpty()) {
            return new PostsVotesResponse(postId, 0);
        }
        return new PostsVotesResponse(postId, byPostAndMember.getFirst().getVote());

    }

    // ===== UPDATE ========================================

    public PostsVotesResponse operatePostVote(Member member, Long postId, PostsVotesRequest voteDTO) {

        Posts targetPost = postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        if (targetPost.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetPost.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

//        List<Member> forumsBans = targetPost.getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
//        boolean isBanned = forumsBans.contains(member);
//        if (isBanned) {
//            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
//        }

        ForumsBans ban = forumsBansRepos
                .findFirstByForumAndMemberOrderByBanIdDesc(targetPost.getForum(), member).orElse(null);
        if (ban != null) {
            Boolean isPenalized = ban.getIsPenalized();
            if (isPenalized) {
                throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
            }
        }

        List<PostsVotes> byPostAndMember = postsVotesRepos.findByPostAndMember(targetPost, member);
        if (!byPostAndMember.isEmpty()) {
            PostsVotes voteRecordObj = byPostAndMember.getFirst();
            Integer voteRecordValue = voteRecordObj.getVote();
            Integer voteRequest = voteDTO.getVote();

            if (voteRequest.equals(voteRecordValue)) {
                voteRecordObj.setVote(0);
                postsVotesRepos.save(voteRecordObj);
                return new PostsVotesResponse(postId, 0);
            }

            if (voteRequest == 1) {
                voteRecordObj.setVote(1);
                postsVotesRepos.save(voteRecordObj);
                return new PostsVotesResponse(postId, 1);
            }

            if (voteRequest == -1) {
                voteRecordObj.setVote(-1);
                postsVotesRepos.save(voteRecordObj);
                return new PostsVotesResponse(postId, -1);
            }
        }

        PostsVotesPK newVotePK = new PostsVotesPK(postId, member.getId());
        PostsVotes newVote = new PostsVotes();

        newVote.setPostsVotesPK(newVotePK);
        newVote.setMember(member);
        newVote.setPost(targetPost);
        newVote.setVote(voteDTO.getVote());
        newVote.setVotedAt(LocalDateTime.now());
        postsVotesRepos.save(newVote);
        return new PostsVotesResponse(postId, newVote.getVote());

    }

}
