package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {

    Event event;
    Debt d1;
    Debt d2;
    Debt d3;
    Participant p1;
    Participant p2;

    @BeforeEach
    void setup() {
        event = new Event("1", "1", LocalDateTime.now());
        p1 = new Participant("s", "t", event);
        p2 = new Participant("u", "v", event);
        d1 = new Debt(p1, p2, 5, "EUR");
        d2 = new Debt(p2, p1, 3, "EUR");
        d3 = new Debt(p1, p2, 5, "EUR");
    }

    @Test
    void notEqualsDebts() {
        assertNotEquals(d1, d2);
    }

    @Test
    void equalsTest() {
        assertEquals(d1, d3);
    }

    /**
     * I don't know why these tests only worked this way.
     */
    @Test
    void getPayer() {
        assertEquals(p1, d1.getPayer());
    }

    @Test
    void getPayee() {
        assertEquals(p2, d1.getPayee());
    }

    @Test
    void getTotal() {
        assertEquals(5, d1.getTotal());
    }

    @Test
    void hasCodeTest() {
        assertNotEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void setPayerTest() {
        d1.setPayer(p1);
        assertEquals(p1, d1.getPayer());
    }

    @Test
    void setPayeeTest() {
        d1.setPayee(p1);
        assertEquals(p1, d1.getPayee());
    }

    @Test
    void setTotalTest() {
        d1.setTotal(543);
        assertEquals(543, d1.getTotal());
    }

    @Test
    void setCurrencyTest() {
        d1.setCurrency("USD");
        assertEquals("USD", d1.getCurrency());
    }

    @Test
    void payInfoTest() {
        d1.setPayer(p1);
        d1.setPayee(p2);
        assertEquals("s must pay u 5.00 EUR.", d1.basicInfo("en"));
    }

    @Test
    void emptyConstructorTest() {
        Debt d = new Debt();
        assertNotNull(d);
    }

    @Test
    void paymentInstructionsUnavailableTest() {
        assertEquals("u chose not to provide bank information.", d1.paymentInstructions("en"));
    }

    @Test
    void paymentInstructionsAvailableTest() {
        p2.setIban("1234");
        p2.setBic("5678");
        assertEquals("""
                        Bank information available, transfer the money to:
                        Account holder: u
                        IBAN: 1234
                        BIC: 5678
                        Amount: 5.00 EUR""",
                d1.paymentInstructions("en"));
    }

    @Test
    void createTransactionTest() {
        Transaction t = d1.createTransaction(5);
        Transaction s = new Transaction(p1, p2, 5, "EUR", LocalDate.now());
        s.setId(t.getId());
        s.setDateOfTransaction(t.getDateOfTransaction());
        assertEquals(s, t);
    }

    @Test
    public void toStringTest() {
        assertTrue(d1.toString().contains("Participant name       : s")
                && d1.toString().contains("Participant name       : u")
                && d1.toString().contains("total=5.0")
                && d1.toString().contains("currency=EUR"));
    }
}
