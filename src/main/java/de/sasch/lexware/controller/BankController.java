package de.sasch.lexware.controller;

import de.sasch.lexware.exception.ApiAccessException;
import de.sasch.lexware.exception.InvalidIbanException;
import de.sasch.lexware.service.BankDTO;
import de.sasch.lexware.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<BankDTO> getBankInfo(@RequestParam("iban") String iban) {

        try {
            return ResponseEntity.ok(bankService.getBankNameFromIban(iban));
        } catch (ApiAccessException apiAccessException) {
            return ResponseEntity.internalServerError().build();
        } catch (InvalidIbanException ibanException) {
            return ResponseEntity.badRequest().build();
        }

    }
}
