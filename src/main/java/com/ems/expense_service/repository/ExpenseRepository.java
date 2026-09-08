package com.ems.expense_service.repository;

import com.ems.expense_service.model.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    Optional<Expense> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    List<Expense> findAllByUserIdAndDeletedFalse(Long userId);

}
