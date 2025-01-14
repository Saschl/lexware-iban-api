package de.sasch.lexware.service;

import de.sasch.lexware.entities.BankEntity;
import de.sasch.lexware.exception.ApiAccessException;
import de.sasch.lexware.exception.InvalidIbanException;
import de.sasch.lexware.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BankServiceTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private BankRepository bankRepository;

    @MockitoBean
    private OpenIBANClient openIBANClient;

    @BeforeEach
    void setUp() {
        bankRepository.deleteAll();
    }

    @Test
    void testGetBankNameFromIban_whenCacheHit_thenReturnEntryFromDB() {
        String iban = "DE89370400440532013000";
        BankEntity bankEntity = new BankEntity();
        bankEntity.setIban(iban);
        bankEntity.setName("Test Bank");
        bankEntity.setExpiresAt(LocalDateTime.now().plusHours(1));
        bankEntity.setValid(true);

        bankRepository.save(bankEntity);

        BankDTO bankDTO = bankService.getBankNameFromIban(iban);

        assertNotNull(bankDTO);
        assertEquals("Test Bank", bankDTO.getBankData().getName());
        assertEquals(1, bankRepository.count());
        verify(openIBANClient, times(0)).getBankData(anyString());
    }

    @Test
    void testGetBankNameFromIban_whenCacheHitButExpired_thenDeleteEntryAndInsertNewOne() {
        String iban = "DE89370400440532013000";
        BankEntity bankEntity = new BankEntity();
        bankEntity.setIban(iban);
        bankEntity.setName("Test Bank");
        bankEntity.setExpiresAt(LocalDateTime.now().minusHours(1));
        bankEntity.setValid(true);

        when(openIBANClient.getBankData(anyString())).thenReturn(createBankDTO());

        bankRepository.save(bankEntity);

        BankDTO bankDTO = bankService.getBankNameFromIban(iban);

        assertNotNull(bankDTO);
        assertEquals("Test Bank", bankDTO.getBankData().getName());
        verify(openIBANClient, times(1)).getBankData(anyString());
        assertEquals(1, bankRepository.count());
        var newCacheEntry = bankRepository.findByIban(iban).get();
        assertTrue(newCacheEntry.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void testGetBankNameFromIban_whenCacheMiss_thenCallAPIAndStoreEntryInDB() {
        String iban = "DE89370400440532013000";
        when(openIBANClient.getBankData(anyString())).thenReturn(createBankDTO());

        BankDTO result = bankService.getBankNameFromIban(iban);

        assertNotNull(result);
        assertEquals("Test Bank", result.getBankData().getName());
        assertEquals(1, bankRepository.count());
        verify(openIBANClient, times(1)).getBankData(anyString());
    }

    @Test
    void testGetBankNameFromIban_whenInvalidIban_thenPassException() {
        String iban = "INVALID_IBAN";
        when(openIBANClient.getBankData(anyString())).thenThrow(InvalidIbanException.class);

        assertThrows(InvalidIbanException.class, () -> bankService.getBankNameFromIban(iban));
        assertEquals(0, bankRepository.count());
        verify(openIBANClient, times(1)).getBankData(anyString());
    }

    @Test
    void testGetBankNameFromIban_whenApiAccessException_thenPassException() {
        String iban = "DE89370400440532013000";
        when(openIBANClient.getBankData(anyString())).thenThrow(new ApiAccessException("Error when accessing OpenIBAN API"));

        assertThrows(ApiAccessException.class, () -> bankService.getBankNameFromIban(iban));
        assertEquals(0, bankRepository.count());
        verify(openIBANClient, times(1)).getBankData(anyString());
    }

    private BankDTO createBankDTO() {
        return BankDTO.builder()
                .valid(true)
                .bankData(BankDTO.BankData.builder().name("Test Bank").build())
                .build();

    }

    private BankDTO createInvalidBankDTO() {
        return BankDTO.builder()
                .valid(false)
                .bankData(BankDTO.BankData.builder().name("").build())
                .build();
    }
}