package game_forum_api.cashTransaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import game_forum_api.cashTransaction.domain.Transactions;
import game_forum_api.member.model.Member;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByMember(Member member);
}

