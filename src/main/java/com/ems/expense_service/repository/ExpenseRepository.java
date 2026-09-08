package com.ems.expense_service.repository;

import com.ems.expense_service.model.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
}
