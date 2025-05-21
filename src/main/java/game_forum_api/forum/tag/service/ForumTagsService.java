package game_forum_api.forum.tag.service;

import game_forum_api.exception.common.ForbiddenException;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.tag.dto.*;
import game_forum_api.forum.tag.model.ForumTags;
import game_forum_api.forum.tag.repository.ForumTagsRepository;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.member.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumTagsService {

    private final ForumTagsRepository forumTagsRepos;

    private final ForumsRepository forumsRepos;

    public ForumTagsService(ForumTagsRepository forumTagsRepos, ForumsRepository forumsRepos) {
        this.forumTagsRepos = forumTagsRepos;
        this.forumsRepos = forumsRepos;
    }

    // ===== CREATE ========================================

    public ForumTagsResponse createForumTag(Member moderator, Integer forumId, ForumTagsRequest forumTag) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<Member> moderators = targetForum.getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        ForumTags newTag = new ForumTags();
        newTag.setName(forumTag.getName());
        newTag.setForum(targetForum);
        newTag.setColor(forumTag.getColor());
        newTag.setIsActive(true);
        forumTagsRepos.save(newTag);

        return ForumTagsMapper.toForumResponseDTO(newTag);

    }

    // ===== RETRIEVE ========================================

    /**
     * 取得所有標籤。<br>
     * 包含 is_active = false。<br>
     * 給版主在設定頁面時顯示。
     */
    public List<ForumTagsResponse> getAllForumTags(Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<ForumTags> allByForum = forumTagsRepos.findByForumOrderByNameAsc(targetForum);
        return ForumTagsMapper.toForumTagsListResponseDTO(allByForum);

    }

    /**
     * 取得 is_active 標籤。<br>
     * 發文頁面與文章列表上方顯示用。
     */
    public List<ForumTagsResponse> getActiveForumTags(Integer forumId) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<ForumTags> activeTagsByForum = forumTagsRepos
                .findByForumAndIsActiveOrderByColorAsc(targetForum, true);
        return ForumTagsMapper.toForumTagsListResponseDTO(activeTagsByForum);

    }

    /**
     * 取得特定 ID 的標籤。
     */
    public ForumTagsResponse getTagByTagId(Long id) {

        ForumTags targetTag = forumTagsRepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標標籤。ID：" + id));
        return ForumTagsMapper.toForumResponseDTO(targetTag);

    }

    // ===== UPDATE ========================================

    public String disableForumTag(Member moderator, Long tagId) {

        ForumTags targetTag = forumTagsRepos.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標標籤。ID：" + tagId));

        System.out.println(tagId);
        System.out.println(targetTag.getName());

        List<Member> moderators = targetTag.getForum().getModerators();
        boolean isModerator = moderators.contains(moderator);
        if (!isModerator) {
            throw new ForbiddenException("使用者沒有操作權限。");
        }

        Boolean isActive = targetTag.getIsActive();
        targetTag.setIsActive(isActive == null || !isActive);
        forumTagsRepos.save(targetTag);

        return "Success";

    }

}
