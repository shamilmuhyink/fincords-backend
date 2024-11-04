package com.fincords.controller;

import com.fincords.dto.respone.ApiResponse;
import com.fincords.model.Pagination;
import com.fincords.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/transaction")
public class TransactionController {

    @Autowired
    TransactionService service;

    @GetMapping("read-all")
    public ResponseEntity<ApiResponse<?>> readAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .build();
        ApiResponse<?> response = service.readAll(pagination);
        return ResponseEntity.ok(response);
    }
}
