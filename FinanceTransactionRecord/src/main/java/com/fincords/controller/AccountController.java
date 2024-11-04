package com.fincords.controller;

import com.fincords.dto.request.AccountRequestDTO;
import com.fincords.dto.respone.ApiResponse;
import com.fincords.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/api/account")
public class AccountController {

    @Autowired
    AccountService service;

    @PostMapping("/create-account")
    public ResponseEntity<ApiResponse<?>> createAccount(@Validated @RequestBody AccountRequestDTO request) {
        ApiResponse<?> response = service.create(request);
        return ResponseEntity.ok(response);
    }
}
