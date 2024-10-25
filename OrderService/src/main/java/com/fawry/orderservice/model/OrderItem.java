package com.fawry.orderservice.model;

import com.fawry.orderservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(updatable = false, nullable = false)
    private int quantity;
    @Column(updatable = false, nullable = false)
    private String productSku;
    @Column(updatable = false, nullable = false)
    private double price;
}
