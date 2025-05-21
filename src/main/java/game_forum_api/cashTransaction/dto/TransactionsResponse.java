package game_forum_api.cashTransaction.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsResponse {
    private Integer transactionId;
    private Integer amount;
    private String type;
    private LocalDateTime transactionDate;
    private String memberName;  // 只回傳會員名稱，避免無限遞迴
}
