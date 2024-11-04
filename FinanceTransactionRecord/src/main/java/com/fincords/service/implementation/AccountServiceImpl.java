package com.fincords.service.implementation;

import com.fincords.dto.request.AccountRequestDTO;
import com.fincords.dto.respone.ApiResponse;
import com.fincords.enumeration.TransactionStatus;
import com.fincords.model.Account;
import com.fincords.model.Profile;
import com.fincords.model.Role;
import com.fincords.model.Transaction;
import com.fincords.repository.AccountRepository;
import com.fincords.repository.ProfileRepository;
import com.fincords.repository.RoleRepository;
import com.fincords.repository.TransactionRepository;
import com.fincords.service.AccountService;
import com.fincords.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ProfileRepository profileRepo;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    TransactionRepository transactionRepo;

    @Transactional
    @Override
    public ApiResponse<?> create(Object request) {
        try {
            AccountRequestDTO accountRequestDTO = (AccountRequestDTO) request;
            String mobileNumber = accountRequestDTO.getMobileNumber();

            log.info("Creating new profile for mobile number: {}", mobileNumber);

            // Find or create user and profile in a single query
            Account account = accountRepo.findByMobileNumber(mobileNumber)
                    .orElseGet(() -> {
                        Account newAccount = new Account();
                        newAccount.setMobileNumber(mobileNumber);
                        return newAccount;
                    });

            Account masterAccount = jwtUtil.extractAccountFromAuthToken().get();

            Profile profile = profileRepo.findByAccountIdAndMaster(account.getId(), masterAccount.getId())
                    .orElseGet(() -> {
                        Profile newProfile = Profile.builder()
                                .name(accountRequestDTO.getName())
                                .account(account)
                                .createdAt(LocalDateTime.now())
                                .createdBy(masterAccount)
                                .isActive(true)
                                .build();
                        return newProfile;
                    });

            log.info("Found or created account: {} and profile: {}", account.getId(), profile.getId());

            // Assign default role if not already assigned
            if (account.getRoles().isEmpty()) {
                Role userRole = roleRepo.findByName("USER")
                        .orElseGet(() -> roleRepo.save(new Role("USER")));
                account.getRoles().add(userRole);
                log.info("Assigned default role to account: {}", account.getId());
            }

            // Save user and profile in a single transaction
            accountRepo.save(account);
            profileRepo.save(profile);

            // Create a new transaction
            Transaction transaction = Transaction.builder()
                    .fromAccount(masterAccount)
                    .toAccount(account)
                    .amount(accountRequestDTO.getAmount())
                    .transactionType(accountRequestDTO.getType())
                    .status(TransactionStatus.PENDING)
                    .transactionTime(LocalDateTime.now())
                    .description(accountRequestDTO.getDescription())
                    .isDeleted(false)
                    .build();

            // Save the transaction
            transactionRepo.save(transaction);

            log.info("Profile created successfully: {}", profile.getId());

            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Profile created successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Failed to create profile: {}", e.getMessage());
            return ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public ApiResponse<?> read(Object request) {
        return null;
    }

    @Override
    public ApiResponse<?> readAll(Object request) {
        return null;
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
