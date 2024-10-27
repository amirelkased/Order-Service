package com.fawry.orderservice.model;

import com.fawry.orderservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "_order")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(updatable = false)
    private String couponCode;
    private boolean couponApplied;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", updatable = false, nullable = false)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    @Column(updatable = false, nullable = false)
    private Long customerId;
    @Column(updatable = false, nullable = false)
    private Long merchant;

    @Column(nullable = false)
    private double totalAmount;
    private double discountAmount;
    private String customerTransactionId;
    private String merchantTransactionId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}

