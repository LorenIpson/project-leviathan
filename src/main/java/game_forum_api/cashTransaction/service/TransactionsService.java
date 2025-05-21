package game_forum_api.cashTransaction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import game_forum_api.cashTransaction.domain.Transactions;
import game_forum_api.cashTransaction.dto.TransactionsRequest;
import game_forum_api.cashTransaction.dto.TransactionsResponse;
import game_forum_api.cashTransaction.repository.TransactionsRepository;
import game_forum_api.exception.common.ResourceNotFoundException;
import game_forum_api.member.model.Member;
import game_forum_api.member.repository.MemberRepository;
import game_forum_api.order.domain.Orders;

@Service
public class TransactionsService {

    private final TransactionsRepository transactionsRepository;
    private final MemberRepository memberRepository;

    public TransactionsService(TransactionsRepository transactionsRepository, MemberRepository memberRepository) {
        this.transactionsRepository = transactionsRepository;
        this.memberRepository = memberRepository;
    }

    // 新增交易
    public TransactionsResponse createTransaction(TransactionsRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        Transactions transaction = new Transactions();
        transaction.setMember(member);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTransactionDate(LocalDateTime.now());

        Transactions savedTransaction = transactionsRepository.save(transaction);

        return new TransactionsResponse(
                savedTransaction.getTransactionId(),
                savedTransaction.getAmount(),
                savedTransaction.getType(),
                savedTransaction.getTransactionDate(),
                savedTransaction.getMember().getUsername()
        );
    }
    
    private TransactionsResponse mapToResponse(Transactions tx) {
        return new TransactionsResponse(
            tx.getTransactionId(),
            tx.getAmount(),
            tx.getType(),
            tx.getTransactionDate(),
            tx.getMember().getUsername()
        );
    }
    
    public List<TransactionsResponse> getAllTransactions() {
        List<Transactions> list = transactionsRepository.findAll();
        return list.stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    // 從 `Orders` 建立 `Transactions`
    public void createTransactionFromOrder(Orders order, String tradeStatus) {
        Transactions transaction = new Transactions();
        transaction.setMember(order.getMember());
        transaction.setAmount(order.getTotalPrice());

        // 根據交易結果設定 `type`
        if ("1".equals(tradeStatus)) {
            transaction.setType("income"); // 交易成功
        } else {
            transaction.setType("refuse"); // 交易失敗
        }

        transaction.setTransactionDate(LocalDateTime.now());
        transactionsRepository.save(transaction);
        System.out.println("交易記錄已新增，交易 ID：" + transaction.getTransactionId() + ", 類型: " + transaction.getType());
    }

    // 查詢會員的所有交易紀錄
    public List<TransactionsResponse> getTransactionsByMemberId(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到會員"));

        List<Transactions> transactions = transactionsRepository.findByMember(member);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("該會員沒有交易紀錄");
        }

        return transactions.stream().map(tx -> new TransactionsResponse(
                tx.getTransactionId(),
                tx.getAmount(),
                tx.getType(),
                tx.getTransactionDate(),
                tx.getMember().getUsername()
        )).toList();
    }

    // 刪除交易
    public void deleteTransaction(Integer transactionId) {
        if (!transactionsRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("找不到該交易紀錄");
        }
        transactionsRepository.deleteById(transactionId);
    }
    
    public void createTransaction(Orders order) {
        Transactions transaction = new Transactions();
        transaction.setMember(order.getMember());
        transaction.setAmount(order.getTotalPrice());
        transaction.setType("income"); // 記錄交易類型

        transactionsRepository.save(transaction);
        System.out.println("交易記錄已新增，交易 ID：" + transaction.getTransactionId());
    }
}

