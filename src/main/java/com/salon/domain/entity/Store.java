package com.salon.domain.entity;

import com.salon.domain.enums.StoreStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "store",
    indexes = {
        @Index(name = "idx_store_status", columnList = "status")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StoreStatus status;

    @Column(nullable = false, length = 50)
    private String timezone;

    private Store(String name, StoreStatus status, String timezone) {
        this.name = name;
        this.status = status;
        this.timezone = timezone;
    }

    public static Store create(String name, StoreStatus status, String timezone) {
        return new Store(name, status, timezone);
    }

    public void update(String name, StoreStatus status, String timezone) {
        this.name = name;
        this.status = status;
        this.timezone = timezone;
    }
}