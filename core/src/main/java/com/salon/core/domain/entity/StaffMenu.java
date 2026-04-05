package com.salon.core.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staff_menu",
    indexes = {
        @Index(name = "idx_staff_menu_staff", columnList = "staff_id"),
        @Index(name = "idx_staff_menu_menu", columnList = "menu_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_staff_menu", columnNames = {"staff_id", "menu_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private ServiceMenu menu;

    private StaffMenu(Staff staff, ServiceMenu menu) {
        this.staff = staff;
        this.menu = menu;
    }

    public static StaffMenu create(Staff staff, ServiceMenu menu) {
        return new StaffMenu(staff, menu);
    }
}