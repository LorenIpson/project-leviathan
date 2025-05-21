package game_forum_api.forum.util;

import game_forum_api.forum.forum.service.ForumsService;
import game_forum_api.forum.post.service.PostsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PopularityCounter {

    private final ForumsService forumsService;

    private final PostsService postsService;

    public PopularityCounter(ForumsService forumsService, PostsService postsService) {
        this.forumsService = forumsService;
        this.postsService = postsService;
    }

    @Scheduled(cron = "0 00 02 * * ?")
    public void setCountForumsPopularityScore() {
        forumsService.updateForumsPopularityScore();
    }

    @Scheduled(cron = "0 00 02 * * ?")
    public void setCountPostsPopularityScore() {
        postsService.updatePostPopularityScore();
    }

}
