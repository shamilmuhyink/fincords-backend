package com.fincords.repository;

import com.fincords.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :acId OR t.toAccount.id = :acId AND t.isDeleted = false ORDER BY t.transactionTime DESC")
    Page<Transaction> findAllByAccountId(@Param("acId") Long id, Pageable pageable);
}
