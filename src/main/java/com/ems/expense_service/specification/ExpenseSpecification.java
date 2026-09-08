package com.ems.expense_service.specification;

import com.ems.expense_service.model.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {
    public static Specification<Expense> buildSpecification(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always enforce ownership and soft delete check
            predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Optional category filter
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            // Optional start date filter (expenseDate >= startDate)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), startDate));
            }

            // Optional end date filter (expenseDate <= endDate)
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
