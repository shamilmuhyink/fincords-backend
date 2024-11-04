package com.fincords.service.implementation;

import com.fincords.dto.respone.ApiResponse;
import com.fincords.dto.respone.TransactionResponseDTO;
import com.fincords.model.Account;
import com.fincords.model.Pagination;
import com.fincords.model.Transaction;
import com.fincords.repository.TransactionRepository;
import com.fincords.service.TransactionService;
import com.fincords.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private TransactionRepository transactionRepo;

    @Override
    public ApiResponse<?> create(Object request) {
        return null;
    }

    @Override
    public ApiResponse<?> read(Object request) {
        return null;
    }

    @Transactional
    @Override
    public ApiResponse<?> readAll(Object request) {
        try {
            Account account = jwtUtil.extractAccountFromAuthToken().get();
            Pagination pagination = (Pagination) request;

            List<Transaction> transactions = new ArrayList<>();
//            Page<Transaction> transactionPage;
            Pageable pageable = PageRequest.of(pagination.getPage() - 1, pagination.getSize());
            Page<Transaction> transactionPage = transactionRepo.findAllByAccountId(account.getId(), pageable);
            transactions.addAll(transactionPage.getContent());

            List<TransactionResponseDTO> responseList = new ArrayList<TransactionResponseDTO>();
            for (Transaction transaction : transactions) {
                TransactionResponseDTO response = TransactionResponseDTO.builder()
                        .id(transaction.getId())
                        .description(transaction.getDescription())
                        .transactionType(transaction.getTransactionType())
                        .fromAccountId(transaction.getFromAccount().getId())
                        .toAccountId(transaction.getToAccount().getId())
                        .amount(transaction.getAmount())
                        .transactionTime(transaction.getTransactionTime())
                        .build();
                responseList.add(response);
            }
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .data(responseList)
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Error reading transactions: {}", e.getMessage(), e);
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public ApiResponse<?> update(Object request) {
        return null;
    }

    @Override
    public ApiResponse<?> delete(Object request) {
        return null;
    }
}
