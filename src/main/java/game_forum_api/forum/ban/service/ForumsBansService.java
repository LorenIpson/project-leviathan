package game_forum_api.forum.ban.service;

import game_forum_api.exception.common.DataConflictException;
import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.ban.dto.*;
import game_forum_api.forum.ban.model.ForumsBans;
import game_forum_api.forum.ban.repository.ForumsBansRepository;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.notification.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ForumsBansService {

    private final MemberRepository memberRepos;

    private final ForumsRepository forumsRepos;

    private final ForumsBansRepository forumBansRepos;

    private final NotificationService notificationService;

    public ForumsBansService(MemberRepository memberRepos,
                             ForumsRepository forumsRepos,
                             ForumsBansRepository forumBansRepos,
                             NotificationService notificationService) {
        this.memberRepos = memberRepos;
        this.forumsRepos = forumsRepos;
        this.forumBansRepos = forumBansRepos;
        this.notificationService = notificationService;
    }

    // ===== CREATE ========================================

    /**
     * 將使用者加入 Forum 的懲罰名單。<br>
     * 需要該 Forum 的 Moderator 權限。
     */
    public ForumBanResponse addToForumBans(Member operator, Integer forumId, ForumBanRequest banDTO) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。ID：" + forumId));

        Member targetAccount = memberRepos.findByAccountId(banDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到懲罰目標使用者。ID：" + banDTO.getAccountId()));

        List<Member> forumModerators = targetForum.getModerators();

        boolean isModerator = forumModerators.contains(operator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        ForumsBans latestBan = forumBansRepos
                .findFirstByForumAndMemberOrderByBanIdDesc(targetForum, targetAccount).orElse(null);

        if (latestBan != null && latestBan.getIsPenalized()) {
            throw new DataConflictException("使用者已經被封鎖。Hehe");
        }

        ForumsBans newBan = new ForumsBans();
        newBan.setForum(targetForum);
        newBan.setMember(targetAccount);
        newBan.setBannedBy(operator);
        newBan.setBannedAt(LocalDateTime.now());
        newBan.setBanReason(banDTO.getReason());
        newBan.setBannedTil(banDTO.getEndDate());
        newBan.setIsPenalized(true);
        forumBansRepos.save(newBan);

        notificationService.createNotification(
                targetAccount.getId(),
                "ban",
                "已被 " + targetForum.getName() + " 管理員懲罰。",
                targetForum.getId().toString()
        ); // TODO: Value: ban，forum/{forumId}。

        return ForumBanMapper.toForumBanResponse(newBan);

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得 Forum 的所有懲罰名單與細節。
     */
    public List<ForumBanResponse> getForumBans(Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。ID：" + forumId));

        List<ForumsBans> forumsBans = targetForum.getForumsBans();
        return ForumBanMapper.toForumBanResponse(forumsBans);

    }

    /**
     * 前端檢查懲罰狀態。<br>
     * 未登入情況下操作 @MemberId 會出現 Null Exception，所以會在 Console 出現錯誤警告。
     */
    public IsBannedResponse isBannedInForum(Member member, Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論板。ID：" + forumId));

        ForumsBans forumBanByMember = forumBansRepos.findFirstByForumAndMemberOrderByBanIdDesc(targetForum, member).orElse(null);

        if (forumBanByMember != null && forumBanByMember.getIsPenalized()) {
            System.out.println("============================== 被 Ban ㄌ ====================================");
            return new IsBannedResponse(Boolean.TRUE);
        }
        System.out.println("============================== 沒被 Ban 耶 ====================================");
        return new IsBannedResponse(Boolean.FALSE);

    }

    // ===== UPDATE ========================================

    /**
     * 更新使用者懲罰狀況。<br>
     * 會保留懲罰紀錄。
     */
    public String updatePenalizedStatus(Member moderator, Integer forumId, ForumBanUpdateRequest banDTO) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        ForumsBans forumsBan = forumBansRepos.findById(banDTO.getBanId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標懲罰紀錄。ID：" + banDTO.getBanId()));

        List<Member> forumModerators = targetForum.getModerators();
        boolean isModerator = forumModerators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        forumsBan.setIsPenalized(false);
        forumsBan.setBanReason("已經手動解除！原本的懲罰原因：" + forumsBan.getBanReason());
        forumBansRepos.save(forumsBan);
        return "成功取消懲罰。";

    }

    // ===== DELETE ========================================

    // TODO: 改成自動更新 is_penalized = false 或不做示範 qq

}
