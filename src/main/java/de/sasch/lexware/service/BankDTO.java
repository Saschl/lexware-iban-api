package de.sasch.lexware.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "BankDTO")
public class BankDTO {

    private final BankData bankData;
    private final boolean valid;

    @Data
    @Builder
    static class BankData {
        private String name;
    }
}


