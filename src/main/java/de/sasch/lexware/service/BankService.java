package de.sasch.lexware.service;

import de.sasch.lexware.entities.BankEntity;
import de.sasch.lexware.exception.ApiAccessException;
import de.sasch.lexware.repository.BankRepository;
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
                var bankInfo = BankDTO.builder()
                        .bankData(BankDTO.BankData
                                .builder()
                                .name(bankInfoFromCache.get().getName())
                                .build())
                        .valid(bankInfoFromCache.get().isValid())
                        .build();
                log.info("Got bank info from DB");

                return Optional.of(bankInfo);
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
            bankEntity.setName(bankInfoFromApi.getBankData().getName());
            bankEntity.setExpiresAt(LocalDateTime.now().plusHours(1L));
            bankEntity.setValid(true);
            bankRepository.save(bankEntity);
            log.info("Got bank info from API and updated cache");
            return bankInfoFromApi;

        } catch (ResourceAccessException e) {
            throw new ApiAccessException("Error when accessing OpenIBAN API");
        }

    }


}
