package com.salon.core.domain.entity;

import com.salon.core.domain.enums.StaffRole;
import com.salon.core.domain.enums.StaffStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "staff",
    indexes = {
        @Index(name = "idx_staff_store", columnList = "store_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_staff_store_name", columnNames = {"store_id", "name"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Staff extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StaffStatus status;

    private Staff(Store store, String name, StaffRole role, StaffStatus status) {
        this.store = store;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public static Staff create(Store store, String name, StaffRole role, StaffStatus status) {
        return new Staff(store, name, role, status);
    }
}
