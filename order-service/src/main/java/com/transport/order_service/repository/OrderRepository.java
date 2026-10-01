package com.transport.order_service.repository;

import com.transport.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends
        JpaRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> { }