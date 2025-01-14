package de.sasch.lexware.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bank_cache")
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long dbId;

    private String name;

    private String iban;

    private boolean isValid;

    private LocalDateTime expiresAt;

}
