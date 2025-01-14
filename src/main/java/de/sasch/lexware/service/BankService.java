package de.sasch.lexware.service;

import de.sasch.lexware.entities.BankEntity;
import de.sasch.lexware.exception.ApiAccessException;
import de.sasch.lexware.repository.BankRepository;
import de.sasch.lexware.util.IBANValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BankService {

    private final OpenIBANClient openIBANClient;
    private final BankRepository bankRepository;

    public BankDTO getBankNameFromIban(String iban) {
        log.info("calling for iban {}", iban);

        var bankInfoFromDb = fetchBankInfoFromDb(iban);

        return bankInfoFromDb.orElseGet(() -> fetchBankInfoFromApi(iban));
    }

    private Optional<BankDTO> fetchBankInfoFromDb(String iban) {
        var bankInfoFromCache = bankRepository.findByIban(iban);
        if (bankInfoFromCache.isPresent()) {
            final boolean isExpired = isCacheEntryExpired(bankInfoFromCache.get());

            if (isExpired) {
                bankRepository.delete(bankInfoFromCache.get());
                return Optional.empty();
            } else {
                log.info("Got bank info from DB");
                return Optional.of(mapEntityToResponse(bankInfoFromCache.get()));
            }
        }
        return Optional.empty();
    }

    private boolean isCacheEntryExpired(BankEntity bankEntity) {
        return bankEntity.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private BankDTO fetchBankInfoFromApi(String iban) {
        try {
            var bankInfoFromApi = openIBANClient.getBankData(iban);

            var bankEntity = new BankEntity();
            bankEntity.setIban(iban);
            bankEntity.setName(bankInfoFromApi.getBankData().getName().isEmpty() ? "Unknown" : bankInfoFromApi.getBankData().getName());
            bankEntity.setExpiresAt(LocalDateTime.now().plusHours(1L));
            bankEntity.setValid(bankInfoFromApi.isValid());
            bankRepository.save(bankEntity);

            log.info("Got bank info from API and updated cache");
            return mapEntityToResponse(bankEntity);

        } catch (ResourceAccessException | ApiAccessException e) {
            log.warn("Error when accessing OpenIBAN API. Will perform simpler local validation");
            var valid = IBANValidator.isIbanValid(iban);

            // do not update cache when validating locally
            return BankDTO.builder()
                    .valid(valid)
                    .bankData(BankDTO.BankData.builder().name("Unknown. Validation was performed locally").build())
                    .build();
        }
    }

    private BankDTO mapEntityToResponse(BankEntity bankEntity) {
        return BankDTO.builder()
                .bankData(BankDTO.BankData.builder().name(bankEntity.getName()).build())
                .valid(bankEntity.isValid())
                .build();
    }

}
