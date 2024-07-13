package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    Event event;
    Participant payer;
    Participant part1;
    List<Participant> participants;
    Map<String, String> types;
    String type1;
    String type2;
    LocalDate date;
    Expense expense1;
    Expense expense2;
    Expense expense3;

    @BeforeEach
    void setup() {
        event = new Event();
        payer = new Participant("1", "1", event);
        part1 = new Participant("2", "2", event);
        participants = new ArrayList<>();
        participants.add(payer);
        participants.add(part1);
        types = new HashMap<>();
        types.put("Drinks", "0xff0000");
        types.put("Food", "0x0000ff");
        date = LocalDate.of(2024, Calendar.FEBRUARY, 25);
        Expense expense = new Expense(event, payer, participants, "Drinks",
                20.0, "EUR", date, true, types);
        expense1 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        expense2 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        expense3 = new Expense(new Event(), payer, "Drinks", 20.0, "EUR", date,
                true, types);

    }

    @Test
    public void testFullConstructor() {
        assertNotNull(expense1);
        assertEquals(payer, expense1.getPayer());
        assertEquals(participants, expense1.getParticipants());
        assertEquals("Drinks", expense1.getTitle());
        assertEquals(20.0, expense1.getAmount());
        assertEquals("EUR", expense1.getCurrency());
        assertEquals(date, expense1.getDate());
        assertTrue(expense1.isSplitEqually());
        assertEquals(types, expense1.getExpenseTypes());
    }

    @Test
    public void testConstructorWithoutOtherParticipants() {
        assertNotNull(expense3);
        assertEquals(payer, expense3.getPayer());
        assertEquals("Drinks", expense3.getTitle());
        assertEquals(20.0, expense3.getAmount());
        assertEquals("EUR", expense3.getCurrency());
        assertEquals(date, expense3.getDate());
        assertTrue(expense3.isSplitEqually());
        assertEquals(types, expense3.getExpenseTypes());
    }

    @Test
    public void testEmptyConstructor() {
        Expense expense = new Expense();
        assertNotNull(expense);
    }

    @Test
    public void testSetters() {
        Expense expense = new Expense();
        expense.setParticipants(participants);
        assertEquals(participants, expense.getParticipants());
        expense.setPayer(payer);
        assertEquals(payer, expense.getPayer());
        expense.setTitle("Drinks");
        assertEquals("Drinks", expense.getTitle());
        expense.setAmount(20.0);
        assertEquals(20.0, expense.getAmount());
        expense.setCurrency("USD");
        assertEquals("USD", expense.getCurrency());
        expense.setDate(date);
        assertEquals(date, expense.getDate());
        expense.setSplitEqually(false);
        assertFalse(expense.isSplitEqually());
        expense.setExpenseTypes(types);
        assertEquals(types, expense.getExpenseTypes());
        expense.setId(1);
        assertEquals(1, expense.getId());
    }

    @Test
    public void testAmountPerPerson() {
        Expense expense = new Expense();
        List<Participant> participants = new ArrayList<>();

        Participant payer = new Participant();
        Participant part1 = new Participant();
        Participant part2 = new Participant();
        participants.add(payer);
        participants.add(part1);
        participants.add(part2);

        double amount = 60.0;

        expense.setPayer(payer);
        expense.setParticipants(participants);
        expense.setAmount(amount);

        assertEquals(20.0, expense.amountPerPerson());
    }

    @Test
    public void testAmountPerPersonFalse() {
        Expense expense = new Expense();
        List<Participant> participants = new ArrayList<>();

        Participant payer = new Participant();
        Participant part1 = new Participant();
        Participant part2 = new Participant();
        participants.add(payer);
        participants.add(part1);
        participants.add(part2);

        double amount = 66.0;

        expense.setPayer(payer);
        expense.setParticipants(participants);
        expense.setAmount(amount);

        assertNotEquals(20.0, expense.amountPerPerson());
    }

    @Test
    public void testEqualsEqualExpenses() {
        Participant payer = new Participant();
        Participant part1 = new Participant();
        List<Participant> participants = new ArrayList<>();
        participants.add(payer);
        participants.add(part1);
        Map<String, String> types = new HashMap<>();
        types.put("Drinks", "0xff0000");
        types.put("Food", "0x0000ff");
        LocalDate date = LocalDate.of(2024, Calendar.FEBRUARY, 25);
        Expense expense1 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        Expense expense2 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        expense2.setUuid(expense1.getUuid());
        assertEquals(expense1, expense2);
    }

    @Test
    public void testEqualsDifferentExpenses() {
        Participant payer = new Participant();
        Participant part1 = new Participant();
        List<Participant> participants = new ArrayList<>();
        participants.add(payer);
        participants.add(part1);
        Map<String, String> types = new HashMap<>();
        types.put("Drinks", "0xff0000");
        types.put("Food", "0x0000ff");
        LocalDate date = LocalDate.of(2024, Calendar.FEBRUARY, 25);
        Expense expense1 = new Expense(new Event(), payer, participants, "Food", 20.0, "EUR", date,
                true, types);
        String type3 = "Sunday Night";
        types.put(type3, "0x00ff00");
        Expense expense2 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date, true, types);

        expense2.setUuid(expense1.getUuid());
        assertNotEquals(expense1, expense2);
    }

    @Test
    public void testWorkingHashCode() {
        Participant payer = new Participant();
        Participant part1 = new Participant();
        List<Participant> participants = new ArrayList<>();
        participants.add(payer);
        participants.add(part1);
        Map<String, String> types = new HashMap<>();
        types.put("Drinks", "0xff0000");
        types.put("Food", "0x0000ff");
        LocalDate date = LocalDate.of(2024, Calendar.FEBRUARY, 25);
        Expense expense1 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        Expense expense2 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        expense2.setUuid(expense1.getUuid());
        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testNotWorkingHashCode() {
        Participant payer = new Participant();
        Participant part1 = new Participant();
        List<Participant> participants = new ArrayList<>();
        participants.add(payer);
        participants.add(part1);
        Map<String, String> types = new HashMap<>();
        types.put("Drinks", "0xff0000");
        types.put("Food", "0x0000ff");
        LocalDate date = LocalDate.of(2024, Calendar.FEBRUARY, 25);
        Expense expense1 = new Expense(new Event(), payer, participants, "Drinks", 20.0, "EUR", date,
                true, types);
        Participant part2 = new Participant();
        participants.add(part2);
        LocalDate date1 = LocalDate.of(2024, Calendar.FEBRUARY, 26);
        String type3 = "Sunday Night";
        types.put(type3, "0x00ff00");
        Expense expense2 = new Expense(new Event(), part1, participants, "Food", 21.0, "USD", date1,
                false, types);
        expense2.setUuid(expense1.getUuid());
        assertNotEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testGetEvent() {
        assertEquals(event, expense1.getEvent());
    }

    @Test
    public void testSetEvent() {
        Event event2 = new Event();
        expense1.setEvent(event2);
        assertEquals(event2, expense1.getEvent());
    }

    @Test
    public void hasToString() {
        assertNotNull(expense1.toString());
    }
}
