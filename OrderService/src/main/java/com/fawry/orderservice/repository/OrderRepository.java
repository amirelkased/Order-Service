package com.fawry.orderservice.repository;

import com.fawry.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findOrdersByCustomerId(Pageable pageable, Long customerId);

    @Query("""
             SELECT o
             FROM Order o
             JOIN o.orderItems
             WHERE o.customerId = :customerId
             AND o.createdAt BETWEEN :from AND :to
            """)
    Page<Order> findAllOrdersBetweenRangeDates(Pageable pageable,
                                               Long customerId,
                                               @Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to);
}