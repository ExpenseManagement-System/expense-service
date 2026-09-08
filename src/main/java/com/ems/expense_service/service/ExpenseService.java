package com.ems.expense_service.service;

import com.ems.expense_service.exception.ResourceNotFoundException;
import com.ems.expense_service.model.dto.CategoryDto;
import com.ems.expense_service.model.dto.ExpenseRequest;
import com.ems.expense_service.model.dto.ExpenseResponse;
import com.ems.expense_service.model.entity.Category;
import com.ems.expense_service.model.entity.Expense;
import com.ems.expense_service.repository.CategoryRepository;
import com.ems.expense_service.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResponse createExpense(Long userId, ExpenseRequest request){
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: "+ request.categoryId()));

        Expense expense = Expense.builder()
                .userId(userId)
                .category(category)
                .merchant(request.merchant())
                .amount(request.amount())
                .expenseDate(request.expenseDate())
                .description(request.description())
                .paymentMethod(request.paymentMethod())
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .userId(expense.getUserId())
                .category(new CategoryDto(expense.getCategory().getId(),
                        expense.getCategory().getName()))
                .merchant(expense.getMerchant())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .paymentMethod(expense.getPaymentMethod())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long userId, Long expenseId) {
        Expense expense=  expenseRepository.findByIdAndUserIdAndDeletedFalse(expenseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense not found with id: "+expenseId+" for this user"));
        return mapToResponse(expense);
    }
}
