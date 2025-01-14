package de.sasch.lexware.repository;

import de.sasch.lexware.entities.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<BankEntity, Long> {

    Optional<BankEntity> findByIban(String iban);
}
