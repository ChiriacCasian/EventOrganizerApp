package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    Event e1;
    Event e2;
    Participant p1;
    Participant p2;
    Participant p3;
    Participant p4;
    Expense ex1;
    Expense ex2;
    Expense ex3;
    Expense ex4;
    Expense ex5;
    Expense ex6;

    @BeforeEach
    void setup() {
        p1 = new Participant("1", "1", e1);
        p2 = new Participant("2", "2", e1);
        p3 = new Participant("3", "3", e1);
        p4 = new Participant("4", "4", e1);
        ex1 = new Expense(e1, p1, "test 1", 10.0, "Euro", null, false, null);
        ex2 = new Expense(e1, p1, "test 2", 15.0, "Euro", null, true, null);
        e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        e1.addParticipant(List.of(p1, p2, p3, p4));
        e1.addExpense(ex1);
        e1.addExpense(ex2);
        p1.addExpensePaid(ex1);
        p1.addExpensePaid(ex2);
        p1.addExpenseInvolved(ex1);
        p1.addExpenseInvolved(ex2);

        e2 = new Event("Test title", "Test description",
                LocalDateTime.now());
        e2.addParticipant(List.of(p1, p2, p3, p4));
        ex3 = new Expense(e1, p1, e1.getParticipants(), "test 3", 20.0, "Euro", null,
                true, null);
        ex4 = new Expense(e1, p2, List.of(p1, p2, p3), "test 3", 30.0, "Euro", null,
                false, null);
        ex5 = new Expense(e1, p3, List.of(p1, p3), "test 3", 10.0, "Euro", null,
                false, null);
        ex6 = new Expense(e1, p4, List.of(p2, p3, p4), "test 3", 27.0, "Euro", null,
                false, null);
        p1.addExpensePaid(ex3);
        p2.addExpensePaid(ex4);
        p3.addExpensePaid(ex5);
        p4.addExpensePaid(ex6);
        p1.addExpenseInvolved(ex3);
        p1.addExpenseInvolved(ex4);
        p1.addExpenseInvolved(ex5);
        p2.addExpenseInvolved(ex3);
        p2.addExpenseInvolved(ex4);
        p2.addExpenseInvolved(ex6);
        p3.addExpenseInvolved(ex3);
        p3.addExpenseInvolved(ex4);
        p3.addExpenseInvolved(ex5);
        p3.addExpenseInvolved(ex6);
        p4.addExpenseInvolved(ex3);
        p4.addExpenseInvolved(ex6);

        e2.addExpense(ex3);
        e2.addExpense(ex4);
        e2.addExpense(ex5);
        e2.addExpense(ex6);
    }

    @Test
    void totalOpenDebtsTest() {
        Debt d1 = new Debt(p1, p2, 10.0, "Euro");
        e1.setDebts(List.of(d1));
        assertEquals(10.0, e1.totalOpenDebts());
    }

    @Test
    void setTitle() {
        assertThrows(IllegalArgumentException.class, () -> e1.setTitle(null));
        e1.setTitle("Test");
        assertEquals("Test", e1.getTitle());
    }

    @Test
    void setDateOfMod() {
        assertThrows(IllegalArgumentException.class, () -> e1.setDateOfModification(null));
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        e1.setDateOfModification(currentDateTime);
        assertEquals(currentDateTime, e1.getDateOfModification());
    }

    @Test
    void setDateOfCreation() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        e1.setDateOfCreation(currentDateTime);
        assertEquals(currentDateTime, e1.getDateOfCreation());
    }

    @Test
    void setParticipants() {
        e1.setParticipants(List.of(p1));
        assertEquals(List.of(p1), e1.getParticipants());
        e1.setParticipants(List.of(p1, p2));
        assertEquals(List.of(p1, p2), e1.getParticipants());
    }

    @Test
    void setExpenses() {
        e1.setExpenses(List.of(ex1));
        assertEquals(List.of(ex1), e1.getExpenses());
        e1.setExpenses(List.of(ex1, ex2));
        assertEquals(List.of(ex1, ex2), e1.getExpenses());
    }

    @Test
    void setDebts() {
        Debt d1 = new Debt();
        e1.setDebts(List.of(d1));
        assertEquals(List.of(d1), e1.getDebts());
    }

    @Test
    void setDescription() {
        e1.setDescription("Test");
        assertEquals("Test", e1.getDescription());
    }

    @Test
    void setInviteCode() {
        assertThrows(IllegalArgumentException.class, () -> e1.setInviteCode(null));
    }

    @Test
    void setCurrency() {
        e1.setCurrency("USD");
        assertEquals("USD", e1.getCurrency());
    }


    @Test
    void addParticipant() {
        assertEquals(e1.addParticipant((Participant) null), 0);
        e1.addParticipant(p1);
        assertEquals(e1.getParticipants(), List.of(p2, p3, p4, p1));
        assertFalse(e1.addParticipant((List<Participant>) null));
    }

    @Test
    void removeParticipant() {
        assertFalse(e1.removeParticipant(new Participant()));
    }

    @Test
    void removeMultipleParticipants() {
        assertFalse(e1.removeParticipant(List.of(new Participant())));
    }

    @Test
    void addExpense() {
        assertFalse(e1.addExpense((Expense) null));
        e1.addExpense(ex1);
        assertEquals(e1.getExpenses(), List.of(ex2, ex1));
    }

    @Test
    void updateExpense() {
        assertTrue(e1.updateExpense(ex1));
    }

    @Test
    void updateExpenseEmpty() {
        assertFalse(e1.updateExpense(new Expense()));
    }

    @Test
    void removeExpense() {
        assertFalse(e1.removeExpense(new Expense()));
    }

    @Test
    void checkConstructor() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
        var a = new Event("title", "hello!", currentDateTime);
        assertEquals("title", a.getTitle());
        assertEquals("hello!", a.getDescription());
        assertEquals(currentDateTime, a.getDateOfCreation());
        var b = new Event("Title", "Hello!", "Euro", currentDateTime);
        assertEquals("Title", b.getTitle());
        assertEquals("Hello!", b.getDescription());
        assertEquals("Euro", b.getCurrency());
        assertEquals(currentDateTime, b.getDateOfCreation());
        var c = new Event();
        assertNotNull(c);
    }

    @Test
    void equalsHashCodeTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        var a = new Event("title", "hello!", currentDateTime);
        var b = new Event("title", "hello!", currentDateTime);
        a.setInviteCode("000000");
        b.setInviteCode("000000");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEqualsHashCodeTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        var a = new Event("title", "hello!", currentDateTime);
        var b = new Event("title", "good bye!", currentDateTime);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hasToStringTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        var a = new Event("title", "hello!", currentDateTime).toString();
        assertTrue(a.contains("\n"));
    }

    @Test
    void equalsTTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        var a = new Event("title", "hello!", currentDateTime);
        var b = new Event("title", "hello!", currentDateTime);
        a.setInviteCode("000000");
        b.setInviteCode("000000");
        assertEquals(a, b);
    }

    @Test
    void equalsFTest() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        var a = new Event("title", "hello!", currentDateTime);
        var b = new Event("title", "good bye!", currentDateTime);
        assertNotEquals(a, b);
    }

    @Test
    void exceptionShareOwedFrom() {
        boolean thrown = false;
        try {
            e2.calculateShareOwedFrom(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void exceptionShareOwedTo() {
        boolean thrown = false;
        try {
            e2.calculateShareOwedTo(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void shareOwedFromTest() {
        assertEquals(15.0, e2.calculateShareOwedFrom(p1));
        assertEquals(14.0, e2.calculateShareOwedFrom(p2));
        assertEquals(24.0, e2.calculateShareOwedFrom(p3));
        assertEquals(5.0, e2.calculateShareOwedFrom(p4));
    }

    @Test
    void shareOwedToTest() {
        assertEquals(15.0, e2.calculateShareOwedTo(p1));
        assertEquals(20.0, e2.calculateShareOwedTo(p2));
        assertEquals(5.0, e2.calculateShareOwedTo(p3));
        assertEquals(18.0, e2.calculateShareOwedTo(p4));
    }

    @Test
    void balanceTest() {
        assertEquals(0.0, e2.calculateBalance(p1));
        assertEquals(6.0, e2.calculateBalance(p2));
        assertEquals(-19.0, e2.calculateBalance(p3));
        assertEquals(13.0, e2.calculateBalance(p4));
    }

    @Test
    void debtOverviewTest() {
        Set<Debt> debts = e2.debtOverview();
        assertTrue(debts.contains(new Debt(p3, p4, 13.0, e2.getCurrency())));
        assertTrue(debts.contains(new Debt(p3, p2, 6.0, e2.getCurrency())));
        assertEquals(2, debts.size());
    }

    @Test
    void inviteCodeTest() {
        assertTrue(Event.validateInviteCode(e1.getInviteCode()));
    }

    @Test
    void validateInviteCode() {
        assertThrows(IllegalArgumentException.class, () -> e1.setInviteCode(null));
        assertThrows(IllegalArgumentException.class, () -> e1.setInviteCode("null"));
        assertThrows(IllegalArgumentException.class, () -> e1.setInviteCode("asdf12"));
        assertDoesNotThrow(() -> e1.setInviteCode("ASDF12"));
    }

    @Test
    void participantToStringTest() {
        assertEquals("1, 2, 3, 4", e1.participantsToString());
        assertNotEquals("1, 2, 3", e1.participantsToString());
        assertNotNull(e1.participantsToString());
        e1.removeParticipant(List.of(p1, p2, p3));
        assertEquals(List.of(p4), e1.getParticipants());
        e1.removeParticipant(p4);
        assertEquals(new ArrayList<Participant>(), e1.getParticipants());
        assertEquals("", e1.participantsToString());
    }

    @Test
    void totalExpenseTest() {
        assertEquals(25.0, e1.totalExpenses());
    }

    @Test
    void shareOwedFromTransactions() {
        p1.addTransactionFrom(new Transaction(p1, p2, 10.0, "Euro", LocalDate.now()));
        assertEquals(5.0, e2.calculateShareOwedFrom(p1));
    }

    @Test
    void shareOwedToTransactions() {
        p1.addTransactionTo(new Transaction(p1, p2, 10.0, "Euro", LocalDate.now()));
        assertEquals(5.0, e2.calculateShareOwedTo(p1));
    }

    // * Tests to calculate adjusted share per person (uneven division)
    @Test
    void oneParticipantTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.0, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p1));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        p1.addExpenseInvolved(ex1);
        ex1.getParticipants().add(p1);

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(0, e1.calculateShareOwedTo(p1));
    }

    @Test
    void twoParticipantsPayerFirstTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.01, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p1, p2));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(5.00, e1.calculateShareOwedTo(p1));
        assertEquals(5.00, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
    }

    @Test
    void twoParticipantsPayerSecondTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.01, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p2, p1));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(5.01, e1.calculateShareOwedTo(p1));
        assertEquals(5.01, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
    }

    @Test
    void threeParticipantsPayerFirstTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.00, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p1, p2, p3));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(6.66, e1.calculateShareOwedTo(p1));
        assertEquals(3.33, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.33, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
    }

    @Test
    void threeParticipantsPayerNotFirstTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.00, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p2, p1, p3));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(6.67, e1.calculateShareOwedTo(p1));
        assertEquals(3.34, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.33, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
    }

    @Test
    void threeParticipantsPayerFirstMod2Test() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.01, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p1, p2, p3));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(6.67, e1.calculateShareOwedTo(p1));
        assertEquals(3.34, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.33, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
    }

    @Test
    void threeParticipantsPayerSecondMod2Test() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.00, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p3, p1, p2));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(6.67, e1.calculateShareOwedTo(p1));
        assertEquals(3.33, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.34, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
    }

    @Test
    void threeParticipantsPayerThirdMod2Test() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.01, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p2, p3, p1));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(e1.getParticipants());

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(6.68, e1.calculateShareOwedTo(p1));
        assertEquals(3.34, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.34, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
    }

    @Test
    void payerNotInListTest() {
        Event e1 = new Event("Test title", "Test description",
                LocalDateTime.now());
        Participant p1 = new Participant("1", "1", e1);
        Participant p2 = new Participant("2", "2", e1);
        Participant p3 = new Participant("3", "3", e1);
        Participant p4 = new Participant("4", "4", e1);
        Expense ex1 = new Expense(e1, p1, "test 1", 10.01, "Euro",
                LocalDate.now(), false, null);
        e1.addParticipant(List.of(p1, p2, p3, p4));
        e1.addExpense(ex1);
        p1.addExpensePaid(ex1);
        for (Participant p : e1.getParticipants()) {
            if (p != p1) p.addExpenseInvolved(ex1);
        }
        ex1.getParticipants().addAll(List.of(p2, p3, p4));

        assertEquals(0, e1.calculateShareOwedFrom(p1));
        assertEquals(10.01, e1.calculateShareOwedTo(p1));
        assertEquals(3.34, e1.calculateShareOwedFrom(p2));
        assertEquals(0, e1.calculateShareOwedTo(p2));
        assertEquals(3.34, e1.calculateShareOwedFrom(p3));
        assertEquals(0, e1.calculateShareOwedTo(p3));
        assertEquals(3.33, e1.calculateShareOwedFrom(p4));
        assertEquals(0, e1.calculateShareOwedTo(p4));
    }

    @Test
    void updateParticipantTest(){
        p1.setName("Josh");
        assertTrue(e1.updateParticipant(p1));
        assertEquals(e1.getParticipants().indexOf(p1),0);
        assertEquals(p1.getEvent(),e1);
        Participant p5 = new Participant("New", "guy", new Event());
        assertTrue(e1.updateParticipant(p5));
    }
}