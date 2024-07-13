package commons;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParticipantTest {
    @Test
    public void checkConstructor() {
        Event event = new Event();
        Participant p = new Participant("casian", "casian12", event);
        assertEquals("casian", p.getName());
        assertEquals("casian12", p.getEmail());
        Participant p2 = new Participant("casian", "casian13", event, "casianCasian", "HelloWorld");
        assertEquals("casian", p2.getName());
        assertEquals("casian13", p2.getEmail());
        assertEquals("casianCasian", p2.getIban());
        assertEquals("HelloWorld", p2.getBic());
        Participant p3 = new Participant("casian", "casian12", event, "SomethingElse", "HelloWorld");
        Participant p4 = new Participant("casian", "casian12", event, "0000111100", "0000111100");
        assertEquals(p4, p3);
        assertEquals(p4, p);
        assertNotEquals(p4.hashCode(), p2.hashCode());
        assertEquals(p3.hashCode(), p.hashCode());
        assertEquals(p4.hashCode(), p3.hashCode());
    }

    @Test
    public void equalsHashCode() {
        Event event = new Event();
        Participant a = new Participant("ab", "b", event);
        Participant b = new Participant("ab", "b", event, "sa", "asf");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        Event event = new Event();
        Participant a = new Participant("a", "b", event);
        Participant b = new Participant("a", "c", event);
        Participant c = new Participant("a", "c", event, "fga", "abc");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertEquals(b, c);
        assertEquals(b.hashCode(), c.hashCode());
    }

    @Test
    public void hasToString() {
        Event event = new Event();
        String string = new Participant("casian", "chiriac", event, "ABNA 0182", "ROREVO").toString();
        assertTrue(string.contains("\n"));
        assertTrue(string.contains("casian"));
        assertTrue(string.contains("chiriac"));
        assertTrue(string.contains("iban"));
        assertTrue(string.contains("bic"));
        System.out.println(string);
        String string2 = new Participant("andrei", "benea", event).toString();
        assertTrue(string2.contains("andrei"));
        assertTrue(string2.contains("chose not to provide bank information"));
        assertTrue(string2.contains("benea"));
        assertFalse(string2.contains("iban"));
        assertFalse(string2.contains("bic"));
        System.out.println(string2);
        String string3 = new Participant("casian", "chiriac", event, "ABNA 0182", "ROREVO").toString();
        String string4 = new Participant("andrei", "benea", event).toString();
        assertNotEquals(string3, string4);
        assertEquals(string, string3);
    }

    @Test
    public void SettersAndHashTest() {
        Event event = new Event();
        Participant p1 = new Participant("casian", "chiriac", event, "ABNA 0182", "ROREVO");
        Participant p2 = new Participant("andrei", "benea", event);
        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
        p2.setName("casian");
        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
        p1.setEmail("benea");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        p1.setEmail("chiriac");
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p2);
    }

    @Test
    public void setters2Test() {
        Event event = new Event();
        Participant p = new Participant("Frank", "frank123", event);
        p.setEmail("email");
        p.setName("name");
        p.setBic("123");
        p.setIban("123");
        p.setId(1);
        assertEquals("name", p.getName());
        assertEquals("email", p.getEmail());
        assertEquals("123", p.getBic());
        assertEquals("123", p.getIban());
        assertEquals(1, p.getId());
        Participant p2 = new Participant();
        p2.setEmail("email");
        p2.setName("name");
        p2.setBic("123");
        p2.setIban("123");
        p2.setId(1);
        assertEquals("name", p2.getName());
        assertEquals("email", p2.getEmail());
        assertEquals("123", p2.getBic());
        assertEquals("123", p2.getIban());
        assertEquals(1, p2.getId());
        Participant p3 = new Participant("Franke", "fr12@gmail.com", event, "NL09812", "8903");
        p3.setEmail("email");
        p3.setName("name");
        p3.setBic("123");
        p3.setIban("123");
        p3.setId(1);
        assertEquals("name", p3.getName());
        assertEquals("email", p3.getEmail());
        assertEquals("123", p3.getBic());
        assertEquals("123", p3.getIban());
        assertEquals(1, p3.getId());
    }

    @Test
    public void TestingToStringWithSetters() {
        Event event = new Event();
        Participant p = new Participant("casian", "chiriac", event, "ABNA 0182", "ROREVO");
        Participant p2 = new Participant("andrei", "benea", event);
        Participant p3 = new Participant("casian", "chiriac", event, "ABNA 0182", "ROREVO");
        p.setEmail("altcv");
        assertNotEquals(p.toString(), p3.toString());
        p3.setEmail("altcv");
        assertEquals(p.toString(), p3.toString());
        assertNotEquals(p2.toString(), p3.toString());
        p3.setBic(null);
        assertNotEquals(p3.toString(), p2.toString());
        p3.setIban(null);
        p2.setName("casian");
        assertNotEquals(p3.toString(), p2.toString());
        p2.setEmail("chiriac");
        assertNotEquals(p2.toString(), p3.toString());
        assertNotEquals(p, p2);
        assertEquals(p, p3);
    }

    @Test
    public void TestingEmailValidator() {
        Event event = new Event();
        Participant participant = new Participant("aaa", "johnDoe@something.something_else", event);
        assertFalse(participant.emailValidator(""));
        assertFalse(participant.emailValidator(null));
        assertTrue(participant.emailValidator("johnDoe@something.something_else"));
        assertTrue(participant.emailValidator(String.valueOf(UUID.randomUUID())));
        assertFalse(participant.emailValidator("incorrect-email@"));
        assertFalse(participant.emailValidator("no-at-sign-and-domain"));
        assertFalse(participant.emailValidator("no-tld@domain"));
        assertTrue(participant.emailValidator("multiple..dots@domain.com"));
        assertTrue(participant.emailValidator("local..part@domain.com"));
    }

    @Test
    public void testUpdates() {
        Event event = new Event();
        Participant p = new Participant("p", "p@gmail.com", event, "IBAN", "BIC");
        Expense expense1 = new Expense();
        Expense expense2 = new Expense();
        List<Expense> exp = new ArrayList<>();
        exp.add(expense1);
        exp.add(expense2);
        p.setExpensesPaid(exp);
        p.setExpensesInvolved(exp);
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> trans = new ArrayList<>();
        trans.add(transaction1);
        trans.add(transaction2);
        p.setTransactionsFrom(trans);
        p.setTransactionsTo(trans);
        assertEquals(exp, p.getExpensesPaid());
        assertEquals(exp, p.getExpensesInvolved());
        assertEquals(event, p.getEvent());
        assertEquals(trans, p.getTransactionsFrom());
        assertEquals(trans, p.getTransactionsTo());
        Transaction transaction3 = new Transaction();
        assertTrue(p.addTransactionFrom(transaction3));
        assertTrue(p.addTransactionTo(transaction3));
        p.updateExpensesPaid(expense1);
        p.updateExpensesInvolved(expense1);
        assertTrue(p.getExpensesPaid().contains(expense1));
        assertTrue(p.getExpensesInvolved().contains(expense1));
        assertTrue(p.updateTransactionFrom(transaction1));
        assertTrue(p.updateTransactionTo(transaction1));
    }

    @Test
    public void testDeleteUndo() {
        Event event1 = new Event("title1", "123", LocalDateTime.now());
        Event event2 = new Event("title2", "123", LocalDateTime.now());
        Participant p1 = new Participant("casian1", "ch", event1);
        Participant p2 = new Participant("casian2", "ch", event1);
        Participant p3 = new Participant("casian2", "ch", event2);
        Participant p4 = new Participant("casian2", "ch2", event2);
        event1.addParticipant(p1);
        event1.addParticipant(p2);
        event2.addParticipant(p3);
        event2.addParticipant(p4);
    }

    @Test
    public void bicValidatorTest() {
        Event event = new Event();
        Participant p = new Participant("Name", "mail", event);
        assertTrue(p.bicValidator("Thoughts"));
        assertTrue(p.bicValidator("AAAAAAlters"));
        assertFalse(p.bicValidator("four"));
        assertTrue(p.bicValidator(null));
        assertTrue(p.bicValidator(""));
        assertFalse(p.bicValidator("7898sequ"));
    }

    @Test
    public void ibanValidatorTest() {
        Event event = new Event();
        Participant p = new Participant("Name", "Mail", event);
        assertTrue(p.ibanValidator(""));
        assertTrue(p.ibanValidator(null));
        assertFalse(p.ibanValidator("LessThan15"));
        assertFalse(p.ibanValidator("&new number to test haha"));
        assertTrue(p.ibanValidator("NL02ABNA0123456789"));
    }

    @Test
    public void validate() {
        Event event = new Event();
        Participant p = new Participant("Name", "franek@gmail.com", event);
        p.setBic("HBUKGB4BXXX");
        p.setIban("NL02ABNA0123456789");
        assertEquals(p.validate(), 1);
        p.setEmail("Invalid");
        assertEquals(p.validate(), 2);
        p.setEmail("ValidMail@gmail.com");
        p.setBic("LOL");
        assertEquals(p.validate(), 4);
        p.setIban("Invalid");
        assertEquals(p.validate(), 3);
    }
}
