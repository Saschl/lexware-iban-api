package de.sasch.lexware.controller;

import de.sasch.lexware.service.BankDTO;
import de.sasch.lexware.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Validates IBAN and get bank name",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The bank information for the supplied iban"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "The provided IBAN is not valid",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @ResponseBody
    public ResponseEntity<BankDTO> getBankInfo(@RequestParam("iban") @NotBlank @Size(max = 34) String iban) {

        return ResponseEntity.ok(bankService.getBankNameFromIban(iban));


    }
}
