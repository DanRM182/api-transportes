package com.transport.order_service.specification;

import com.transport.order_service.dto.request.OrderFilterRequest;
import com.transport.order_service.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class OrderSpecifications {
    public static Specification<Order> filterOrders(OrderFilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        filters.status()
                ));
            }

            if (filters.origin() != null && !filters.origin().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("origin")),
                        "%" + filters.origin().trim().toLowerCase() + "%"
                ));
            }

            if (filters.destination() != null && !filters.destination().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("destination")),
                        "%" + filters.destination().trim().toLowerCase() + "%"
                ));
            }

            if (filters.createdFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filters.createdFrom()
                ));
            }

            if (filters.createdTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filters.createdTo()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}