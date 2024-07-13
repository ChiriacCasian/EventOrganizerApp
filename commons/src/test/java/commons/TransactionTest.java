package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    Participant part1;
    Participant part2;
    double amount;
    String currency;
    LocalDate date;
    Transaction transaction;

    @BeforeEach
    void setup() {
        part1 = new Participant();
        part2 = new Participant();
        amount = 10;
        currency = "Euro";
        date = LocalDate.now();
        transaction = new Transaction(part1, part2, amount, currency, date);
    }

    @Test
    void setters() {
        Transaction transaction1 = new Transaction();
        transaction1.setId("a");
        assertEquals("a", transaction1.getId());
        transaction1.setDateOfTransaction(date);
        assertEquals(date, transaction1.getDateOfTransaction());
        transaction1.setAmount(amount);
        assertEquals(amount, transaction1.getAmount());
        transaction1.setPayer(part1);
        assertEquals(part1, transaction1.getPayer());
        transaction1.setPayee(part2);
        assertEquals(part2, transaction1.getPayee());
        transaction1.setCurrency(currency);
        assertEquals(currency, transaction1.getCurrency());
    }

    @Test
    void equalsTest() {
        Transaction transaction1 = transaction;
        assertEquals(transaction1, transaction);
        assertEquals(transaction1.hashCode(), transaction.hashCode());
    }

}