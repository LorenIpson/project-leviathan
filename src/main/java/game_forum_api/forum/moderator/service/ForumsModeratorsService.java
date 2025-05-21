package game_forum_api.forum.moderator.service;

import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.moderator.dto.ForumModMapper;
import game_forum_api.forum.moderator.dto.ForumModRequest;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.forum.moderator.dto.ForumModResponse;
import game_forum_api.forum.moderator.dto.IsModeratorResponse;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumsModeratorsService {

    private final ForumsRepository forumsRepos;

    private final MemberRepository memberRepos;

    private final NotificationService notificationService;

    public ForumsModeratorsService(ForumsRepository forumsRepos,
                                   MemberRepository memberRepos,
                                   NotificationService notificationService) {
        this.forumsRepos = forumsRepos;
        this.memberRepos = memberRepos;
        this.notificationService = notificationService;
    }

    // ===== CREATE ========================================

    /**
     * 新增新的 Forum Moderator。<br>
     * 需要 Admin 或該 Forum 的 Moderator 權限。
     */
    public String addForumModerator(Member operator, Integer forumId, ForumModRequest newModeratorDTO) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。"));

        Member targetAccount = memberRepos.findByAccountId(newModeratorDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標新管理者，管理者 ID："
                                                                 + newModeratorDTO.getAccountId()));

        List<Member> forumModerators = targetForum.getModerators();

        boolean isModerator = forumModerators.contains(operator);
        boolean isAdmin = operator.getRole() == 3;

        if (!isModerator && !isAdmin) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        if (forumModerators.contains(targetAccount)) {
            throw new DataConflictException("使用者已經是管理員。");
        }

        forumModerators.add(targetAccount);
        forumsRepos.save(targetForum);

        notificationService.createNotification(
                targetAccount.getId(),
                "moderator",
                "已被指派為 " + targetForum.getName() + " 的管理員。",
                targetForum.getId().toString()
        ); // TODO: Value: moderator，跳轉到 forum/{forumId}。

        return "新的管理員為：" + newModeratorDTO.getAccountId();

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得 Forum 所有的 Moderators。
     */
    public ForumModResponse getModeratorsByForum(Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));
        return ForumModMapper.toForumModResponse(targetForum);

    }

    /**
     * 前端檢查管理員權限。<br>
     * 未登入情況下操作 @MemberId 會出現 Null Exception，所以會在 Console 出現錯誤警告。
     */
    public IsModeratorResponse isForumModerator(Member member, Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<Member> forumModerators = targetForum.getModerators();
        boolean isModerator = forumModerators.contains(member);
        return new IsModeratorResponse(isModerator);

    }

    // ===== DELETE ========================================

    /**
     * 移除 Forum 的 Moderator。<br>
     * 需要 Admin 或該 Forum 的 Moderator 權限。
     */
    public String deleteForumModerator(Member operator, Integer forumId, ForumModRequest moderatorDTO) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。"));

        Member targetModerator = memberRepos.findByAccountId(moderatorDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標使用者。ID：" + moderatorDTO.getAccountId()));

        List<Member> forumModerators = targetForum.getModerators();

        boolean isModerator = forumModerators.contains(operator);
        boolean isAdmin = operator.getRole() == 3;
        if (!isModerator && !isAdmin) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        forumModerators.remove(targetModerator);
        forumsRepos.save(targetForum);

        notificationService.createNotification(
                targetModerator.getId(),
                "moderator",
                "已被移出 " + targetForum.getName() + " 管理員。",
                targetForum.getId().toString()
        );

        return "以移除管理員：" + moderatorDTO.getAccountId();

    }

}
