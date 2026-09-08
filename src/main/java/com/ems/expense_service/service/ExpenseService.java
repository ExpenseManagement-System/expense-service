package com.ems.expense_service.service;

import com.ems.expense_service.exception.ResourceNotFoundException;
import com.ems.expense_service.model.dto.CategoryDto;
import com.ems.expense_service.model.dto.ExpenseRequest;
import com.ems.expense_service.model.dto.ExpenseResponse;
import com.ems.expense_service.model.dto.PagedResponse;
import com.ems.expense_service.model.entity.Category;
import com.ems.expense_service.model.entity.Expense;
import com.ems.expense_service.repository.CategoryRepository;
import com.ems.expense_service.repository.ExpenseRepository;
import com.ems.expense_service.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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


    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> getExpenses(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Expense> spec = ExpenseSpecification.buildSpecification(userId, categoryId, startDate, endDate);

        Page<Expense> expensePage = expenseRepository.findAll(spec, pageable);

        // Map Page<Expense> -> Page<ExpenseResponse>
        Page<ExpenseResponse> responsePage =  expensePage.map(this::mapToResponse);
        return PagedResponse.from(responsePage);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, Long userId, ExpenseRequest request) {
        // Fetch expense and verify user ownership + non-deleted status
        Expense expense = expenseRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id + " for this user"));

        // If category is updated. Fetch it from db and update if present.
        if(!(expense.getCategory().getId().equals(request.categoryId()))){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: "+ request.categoryId()));
            expense.setCategory(category);
        }
        // Update other expense fields
        expense.setMerchant(request.merchant());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setDescription(request.description());
        expense.setPaymentMethod(request.paymentMethod());

        // Save updated entity (updatedAt timestamp automatically managed by Hibernate)
        Expense updatedExpense = expenseRepository.save(expense);
        return mapToResponse(updatedExpense);
    }

    @Transactional
    public void deleteExpense(Long id, Long userId) {
        Expense expense = expenseRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id + " for this user"));

        expense.setDeleted(true);
        expenseRepository.save(expense);
    }
}
