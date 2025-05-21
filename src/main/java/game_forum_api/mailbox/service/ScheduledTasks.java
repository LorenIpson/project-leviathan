package game_forum_api.mailbox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final MailboxService mailboxService;

    @Autowired
    public ScheduledTasks(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }


    //    30秒執行一次，測試用(cron = "0/30 * * * * ?")
    // 每天凌晨 2 點執行一次
    @Scheduled (cron = "0 0 2 * * ?")
    public void deleteOldMails() {
        mailboxService.deleteMailsOlderThan30Days();
        System.out.println("已刪除超過 30 天的信件");
    }
}