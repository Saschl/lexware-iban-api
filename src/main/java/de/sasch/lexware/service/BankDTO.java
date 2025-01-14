package de.sasch.lexware.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankDTO {

    private final BankData bankData;
    private final boolean valid;

    @Data
    @Builder
    static class BankData {
        private String bankCode;
        private String name;
    }
}


