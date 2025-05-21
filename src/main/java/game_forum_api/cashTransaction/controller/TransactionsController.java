package game_forum_api.cashTransaction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import game_forum_api.cashTransaction.dto.TransactionsRequest;
import game_forum_api.cashTransaction.dto.TransactionsResponse;
import game_forum_api.cashTransaction.service.TransactionsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    // 新增交易
    @PostMapping("/create")
    public ResponseEntity<TransactionsResponse> createTransaction(@RequestBody @Valid TransactionsRequest request) {
        TransactionsResponse response = transactionsService.createTransaction(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<TransactionsResponse>> getAllTransactions() {
        List<TransactionsResponse> response = transactionsService.getAllTransactions();
        return ResponseEntity.ok(response);
    }

    // 查詢會員的交易紀錄
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TransactionsResponse>> getTransactionsByMember(@PathVariable Integer memberId) {
        List<TransactionsResponse> response = transactionsService.getTransactionsByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

    // 刪除交易
    @DeleteMapping("/delete/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Integer transactionId) {
        transactionsService.deleteTransaction(transactionId);
        return ResponseEntity.ok("交易紀錄已刪除");
    }
}

