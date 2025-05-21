package game_forum_api.forum.forum.service;

import game_forum_api.exception.common.BadRequestException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.forum.dto.ForumDetailMapper;
import game_forum_api.forum.forum.dto.ForumDetailRequest;
import game_forum_api.forum.forum.dto.ForumDetailResponse;
import game_forum_api.forum.forum.model.ForumDetail;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumDetailRepository;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ForumDetailService {

    private final ForumDetailRepository forumDetailRepos;

    private final ForumsRepository forumsRepos;

    public ForumDetailService(ForumDetailRepository forumDetailRepos, ForumsRepository forumsRepos) {
        this.forumDetailRepos = forumDetailRepos;
        this.forumsRepos = forumsRepos;
    }

    // ===== CREATE ========================================

    public ForumDetailResponse createForumDetail(Member operator,
                                                 ForumDetailRequest forumDetailRequest,
                                                 Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。ID：" + forumId));

        List<Member> forumModerators = targetForum.getModerators();

        boolean isModerator = forumModerators.contains(operator);
        boolean isAdmin = operator.getRole() == 3;
        if (!isModerator && !isAdmin) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        ForumDetail newForumDetail = new ForumDetail();
        newForumDetail.setForum(targetForum);

        try {
            newForumDetail.setCover(forumDetailRequest.getCover().getBytes());
        } catch (IOException e) {
            throw new BadRequestException("圖片上傳失敗。");
        }

        newForumDetail.setDescription(forumDetailRequest.getDescription());
        forumDetailRepos.save(newForumDetail);

        return ForumDetailMapper.toDetailResponse(newForumDetail);

    }

    // ===== RETRIEVE ========================================

    public ForumDetailResponse getForumDetail(Integer forumId) {

        ForumDetail targetForum = forumDetailRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        return ForumDetailMapper.toDetailResponse(targetForum);

    }

    // ===== UPDATE ========================================

    public String updateForumDetail(Member moderator,
                                    Integer forumId,
                                    MultipartFile newSecondaryCover,
                                    String newDescription) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<Member> forumModerators = targetForum.getModerators();
        boolean isModerator = forumModerators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        ForumDetail targetDetail = targetForum.getForumDetail();
        targetDetail.setDescription(newDescription);

        if (newSecondaryCover != null) {
            try {
                targetDetail.setCover(newSecondaryCover.getBytes());
            } catch (IOException e) {
                throw new BadRequestException("圖片上傳失敗。");
            }
        }

        forumDetailRepos.save(targetDetail);

        return "更新細節成功。";

    }

}
