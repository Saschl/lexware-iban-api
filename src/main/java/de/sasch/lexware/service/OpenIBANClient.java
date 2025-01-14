package de.sasch.lexware.service;

import de.sasch.lexware.exception.ApiAccessException;
import de.sasch.lexware.exception.InvalidIbanException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenIBANClient {

    private final RestClient restClient = RestClient.create();

    private static final String openIbanUrl = "https://openiban.com/validate/{iban}?getBIC=true&validateBankCode=true";


    public BankDTO getBankData(String iban) {
        var bankInfoFromApi = restClient.get().uri(openIbanUrl, iban).retrieve().body(BankDTO.class);
        if (bankInfoFromApi == null) {
            throw new ApiAccessException("Response from API is null");
        }

        if (!bankInfoFromApi.isValid()) {
            throw new InvalidIbanException("Provided IBAN is not valid");
        }

        return bankInfoFromApi;
    }
}
