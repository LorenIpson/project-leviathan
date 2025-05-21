package game_forum_api.mailbox.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import game_forum_api.mailbox.model.Mailbox;

@Repository
public interface MailboxRepository extends JpaRepository<Mailbox, Integer> {

    // 根據 receiverId 查詢信件，並按照 sendTime 降序排列
    List<Mailbox> findByReceiverIdOrderBySendTimeDesc(Integer receiverId);
    
    // 根據寄件者查詢信件，並按照 sendTime 降序排列
    List<Mailbox> findBySenderIdOrderBySendTimeDesc(Integer senderId);


    // 根據 id 查找單封信件
    Optional<Mailbox> findById(Integer id);

    @Modifying
    @Query("DELETE FROM Mailbox m WHERE m.sendTime < :cutoffDate")
    void deleteMailsOlderThan(Date cutoffDate);
}