package com.ems.expense_service.controller;

import com.ems.expense_service.model.dto.ExpenseRequest;
import com.ems.expense_service.model.dto.ExpenseResponse;
import com.ems.expense_service.model.dto.PagedResponse;
import com.ems.expense_service.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse response = expenseService.createExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long expenseId){
        ExpenseResponse response = expenseService.getExpenseById(userId, expenseId);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<PagedResponse<ExpenseResponse>> getExpenses(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "expenseDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PagedResponse<ExpenseResponse> response = expenseService.getExpenses(
                userId, categoryId, startDate, endDate, pageable
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse response = expenseService.updateExpense(id, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        expenseService.deleteExpense(id, userId);
        return ResponseEntity.noContent().build();
    }
}
