package com.ems.expense_service.model.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ExpenseResponse(
        Long id,
        Long userId,
        CategoryDto category,
        String merchant,
        BigDecimal amount,
        LocalDate expenseDate,
        String description,
        String paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
