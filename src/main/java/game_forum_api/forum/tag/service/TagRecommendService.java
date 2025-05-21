package game_forum_api.forum.tag.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.forum.forum.model.Forums;
import game_forum_api.forum.forum.repository.ForumsRepository;
import game_forum_api.forum.tag.dto.APIRequest;
import game_forum_api.forum.tag.dto.APIMessage;
import game_forum_api.forum.tag.model.ForumTags;
import game_forum_api.forum.tag.repository.ForumTagsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagRecommendService {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o";
    private static final Double TEMPERATURE = 0.0;

    @Value("${openai.api.key}")
    private String key;

    private final ForumTagsRepository forumTagsRepos;

    private final ForumsRepository forumsRepos;

    public TagRecommendService(ForumTagsRepository forumTagsRepos, ForumsRepository forumsRepos) {
        this.forumTagsRepos = forumTagsRepos;
        this.forumsRepos = forumsRepos;
    }

    // ===== UTIL ========================================

    public String tagRecommend(Integer forumId, String promptText) {

        Forums targetForum = forumsRepos.findById(forumId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到目標討論區。ID：" + forumId));

        List<ForumTags> activeTagsByForum = forumTagsRepos.findByForumAndIsActive(targetForum, true);

        String tagList = activeTagsByForum.stream()
                .map(ForumTags::getName)
                .collect(Collectors.joining("，"));

        APIMessage roleSystem = new APIMessage("system", "根據文章標題推薦現有的多個 tags，" +
                                                         "可同時推薦多個且回傳方式為：" +
                                                         "標籤名稱一，標籤名稱二。" +
                                                         "除了名稱外不加入任何說明文字，只用逗號分隔，不加入句號，" +
                                                         "不可以推薦不存在的標籤。");
        APIMessage roleUser = new APIMessage("user",
                "使用者輸入為：" + promptText + "可用 Tags 為：" + tagList);
        List<APIMessage> messages = List.of(roleSystem, roleUser);

        RestClient restClient = RestClient.create();
        APIRequest apiRequest = new APIRequest();
        apiRequest.setModel(MODEL);
        apiRequest.setMessages(messages);
        apiRequest.setTemperature(TEMPERATURE);
        apiRequest.setMax_tokens(1000);
        apiRequest.setTop_p(1);
        apiRequest.setFrequency_penalty(0);
        apiRequest.setPresence_penalty(0);

        String responseBody = restClient.post()
                .uri(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + key)
                .body(apiRequest)
                .retrieve()
                .body(String.class);

        ObjectMapper objMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objMapper.readTree(responseBody);
            String recommendMessage = jsonNode.path("choices").get(0).path("message").path("content").asText();
            // TODO:  調查.textValue 對比 .asText。
            return "猜您需要這些標籤... " + recommendMessage + "。";
        } catch (JsonProcessingException e) {
            return null;
        }

    }

}
