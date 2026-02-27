package com.salon.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation_customer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_res_customer_phone_hash", columnNames = "phone_hash")
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationCustomer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_customer_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String phoneHash;

    @Column(length = 100)
    private String email;

    private ReservationCustomer(String name, String phone, String phoneHash, String email) {
        this.name = name;
        this.phone = phone;
        this.phoneHash = phoneHash;
        this.email = email;
    }

    public static ReservationCustomer create(String name, String phone, String phoneHash, String email) {
        return new ReservationCustomer(name, phone, phoneHash, email);
    }
}
