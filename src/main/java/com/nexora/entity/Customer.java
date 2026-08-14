package com.nexora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Represents a customer registered in the NEXORA system.
 *
 * <p>
 * This entity is mapped to the {@code customers} database table and stores
 * the customer's identity and contact information.
 * </p>
 *
 * @author Büşra
 * @since 1.0.0
 */
@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    /**
     * Unique identifier of the customer.
     *
     * <p>
     * The value is generated automatically by PostgreSQL when a customer
     * record is inserted.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Customer's first name.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Customer's last name.
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Customer's unique email address.
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Customer's optional phone number.
     */
    @Column(name = "phone", length = 30)
    private String phone;

    /**
     * Date and time when the customer record was created.
     *
     * <p>
     * This field is populated automatically and cannot be changed after
     * the initial insert.
     * </p>
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date and time when the customer record was last modified.
     *
     * <p>
     * This field is updated automatically whenever the entity changes.
     * </p>
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}