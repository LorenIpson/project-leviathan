package game_forum_api.forum.vote.service;

import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.comment.model.Comments;
import game_forum_api.forum.comment.repository.CommentsRepository;
import game_forum_api.forum.post.repository.PostsRepository;
import game_forum_api.forum.vote.dto.CommentsVotesRequest;
import game_forum_api.forum.vote.dto.CommentsVotesResponse;
import game_forum_api.forum.vote.model.CommentsVotes;
import game_forum_api.forum.vote.model.CommentsVotesPK;
import game_forum_api.forum.vote.repository.CommentsVotesRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentsVotesService {

    private final CommentsVotesRepository commentsVotesRepos;

    private final CommentsRepository commentsRepos;

    private final PostsRepository postsRepos;

    public CommentsVotesService(CommentsVotesRepository commentsVotesRepos,
                                CommentsRepository commentsRepos,
                                PostsRepository postsRepos) {
        this.commentsVotesRepos = commentsVotesRepos;
        this.commentsRepos = commentsRepos;
        this.postsRepos = postsRepos;
    }

    // ===== CREATE ========================================

    @Deprecated
    public String commentVote(Member member, Long commentId, CommentsVotesRequest voteDTO) {

        Comments targetComment = commentsRepos.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + commentId));

        if (targetComment.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetComment.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

        List<Member> forumsBans = targetComment
                .getPost().getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
        boolean isBanned = forumsBans.contains(member);
        if (isBanned) {
            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
        }

        CommentsVotesPK newVotePK = new CommentsVotesPK(commentId, member.getId());
        CommentsVotes newVote = new CommentsVotes();

        newVote.setCommentsVotesPK(newVotePK);
        newVote.setMember(member);
        newVote.setComment(targetComment);
        newVote.setVote(voteDTO.getVote());
        newVote.setVotedAt(LocalDateTime.now());
        commentsVotesRepos.save(newVote);

        return "（測試用）評分成功。";

    }

    // ===== RETRIEVE ========================================

    public List<CommentsVotesResponse> getMemberCommentVote(Member member, Long postId) {

        postsRepos.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + postId));

        List<CommentsVotes> votes = commentsVotesRepos.findByComment_Post_IdAndMember(postId, member);
        return votes.stream()
                .map(vote -> new CommentsVotesResponse(vote.getComment().getId(), vote.getVote()))
                .toList();

    }

    // ===== UPDATE ========================================

    public CommentsVotesResponse operateCommentVote(Member member, Long commentId, CommentsVotesRequest voteDTO) {

        Comments targetComment = commentsRepos.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標文章。ID：" + commentId));

        if (targetComment.getIsLocked()) {
            throw new ResourceNotFoundException("目標文章已經鎖定。");
        }

        if (targetComment.getIsDeleted()) {
            throw new ResourceNotFoundException("目標文章已經刪除。");
        }

        List<Member> forumsBans = targetComment
                .getPost().getForum().getForumsBans().stream().map(ForumsBans::getMember).toList();
        boolean isBanned = forumsBans.contains(member);
        if (isBanned) {
            throw new ForbiddenException("使用者被懲罰期間沒有操作權限。");
        }

        List<CommentsVotes> byCommentAndMember = commentsVotesRepos.findByCommentAndMember(targetComment, member);
        if (!byCommentAndMember.isEmpty()) {
            CommentsVotes voteRecordObj = byCommentAndMember.getFirst();
            Integer voteRecordValue = voteRecordObj.getVote();
            Integer voteRequest = voteDTO.getVote();

            if (voteRequest.equals(voteRecordValue)) {
                voteRecordObj.setVote(0);
                commentsVotesRepos.save(voteRecordObj);
                return new CommentsVotesResponse(commentId, 0);
            }

            if (voteRequest == 1) {
                voteRecordObj.setVote(1);
                commentsVotesRepos.save(voteRecordObj);
                return new CommentsVotesResponse(commentId, 1);
            }

            if (voteRequest == -1) {
                voteRecordObj.setVote(-1);
                commentsVotesRepos.save(voteRecordObj);
                return new CommentsVotesResponse(commentId, -1);
            }
        }

        CommentsVotesPK newVotePK = new CommentsVotesPK(commentId, member.getId());
        CommentsVotes newVote = new CommentsVotes();

        newVote.setCommentsVotesPK(newVotePK);
        newVote.setMember(member);
        newVote.setComment(targetComment);
        newVote.setVote(voteDTO.getVote());
        newVote.setVotedAt(LocalDateTime.now());
        commentsVotesRepos.save(newVote);
        return new CommentsVotesResponse(commentId, newVote.getVote());

    }

}
