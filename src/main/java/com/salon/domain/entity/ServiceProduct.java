package com.salon.domain.entity;

import com.salon.domain.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_product",
    indexes = {
        @Index(name = "idx_product_store", columnList = "store_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_product_store_name", columnNames = {"store_id", "name"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer durationMin;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Builder
    public ServiceProduct(Store store, String name, Integer durationMin, Integer price, ProductStatus status) {
        this.store = store;
        this.name = name;
        this.durationMin = durationMin;
        this.price = price;
        this.status = status;
    }
}