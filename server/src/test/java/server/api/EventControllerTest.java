package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.services.EventService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class EventControllerTest {
    EventController controller;
    LocalDateTime dateTime;
    LocalDate date;
    ParticipantRepository participantRepo;
    ExpenseRepository expenseRepo;

    @BeforeEach
    void setup() {
        participantRepo = new ParticipantRepositoryTest();
        expenseRepo = new ExpenseRepositoryTest();
        EventRepositoryTest eventRepo = new EventRepositoryTest(expenseRepo, participantRepo);
        EventService eventService = new EventService(eventRepo, participantRepo, expenseRepo);
        controller = new EventController(eventService);
        dateTime = LocalDateTime.now();
        date = LocalDate.now();
    }

    @Test
    void cannotAddNullEvent() {
        var actual = controller.add(getEvent(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void cannotAddNullEventWebsocket() {
        assertNull(controller.addWebSocket(getEvent(null)));
    }


    @Test
    public void getById() {
        Event e1 = getEvent("q1");
        controller.add(e1);
        assertEquals(e1, controller.getById(e1.getInviteCode()).getBody());
    }

    @Test
    public void getAll() {
        Event e1 = getEvent("e1");
        Event e2 = getEvent("e2");
        controller.add(e1);
        controller.add(e2);
        assertEquals(2, controller.getAll().size());
        assertTrue(controller.getAll().contains(e1));
        assertTrue(controller.getAll().contains(e2));
    }


    @Test
    public void updateTest() {
        Event e1 = getEvent("e1");
        controller.add(e1);
        assertEquals("e1", controller.getById(e1.getInviteCode()).getBody().getTitle());
        Event e2 = getEvent("e2");
        e2.setInviteCode(e1.getInviteCode());
        controller.update(e1.getInviteCode(), e2);
        assertEquals(e2, controller.getById(e1.getInviteCode()).getBody());
    }

    @Test
    public void updateWebsocketTest() {
        Event e1 = getEvent("e1");
        controller.add(e1);
        assertEquals("e1", controller.getById(e1.getInviteCode()).getBody().getTitle());
        Event e2 = getEvent("e2");
        e2.setInviteCode(e1.getInviteCode());
        controller.updateWebSocket(e2);
        assertEquals(e2, controller.getById(e1.getInviteCode()).getBody());
    }

    @Test
    public void updateInvalidTest() {
        Event e1 = getEvent("e1");
        controller.add(e1);
        assertEquals("e1", controller.getById(e1.getInviteCode()).getBody().getTitle());
        Event e2 = getEvent("");
        e2.setInviteCode(e1.getInviteCode());
        assertEquals(BAD_REQUEST, controller.update(e1.getInviteCode(), e2).getStatusCode());
    }

    @Test
    public void deleteTest() {
        Event e1 = getEvent("e1");
        controller.add(e1);
        Event e2 = controller.delete(e1.getInviteCode()).getBody();
        assertEquals(e1, e2);
        assertEquals(NOT_FOUND, controller.getById(e1.getInviteCode()).getStatusCode());
    }

    @Test
    public void deleteDoesNotExistTest() {
        Event e1 = getEvent("e1");
        assertEquals(NOT_FOUND, controller.delete(e1.getInviteCode()).getStatusCode());
    }

    @Test
    public void importTest() {
        Event e1 = getEvent("e1");
        String code = e1.getInviteCode();
        controller.add(e1);
        Event e2 = getEvent("e2");
        e2.setInviteCode(code);
        controller.importEvent(e2);
        assertEquals(e2, controller.getById(code).getBody());
        assertNotEquals(e1, controller.getById(code).getBody());
        assertEquals(1, controller.getAll().size());
    }

    @Test
    public void importWebsocketTest() {
        Event e1 = getEvent("e1");
        String code = e1.getInviteCode();
        controller.add(e1);
        Event e2 = getEvent("e2");
        e2.setInviteCode(code);
        controller.importWebSocket(e2);
        assertEquals(e2, controller.getById(code).getBody());
        assertNotEquals(e1, controller.getById(code).getBody());
        assertEquals(1, controller.getAll().size());
    }

    @Test
    public void importInvalidTest() {
        Event e1 = getEvent(null);
        assertEquals(BAD_REQUEST, controller.importEvent(e1).getStatusCode());
    }

    @Test
    public void addEventWithSameInviteCode() {
        Event e1 = getEvent("e1");
        controller.add(e1);
        Event e2 = getEvent("e2");
        e2.setInviteCode(e1.getInviteCode());
        controller.add(e2);
        assertTrue(controller.getAll().contains(e1));
        assertTrue(controller.getAll().contains(e2));
        assertNotEquals(e2.getInviteCode(), e1.getInviteCode());
    }

    @Test
    public void saveEventWithCollections() {
        Event e1 = getEvent("e1");
        Participant p1 = new Participant("p1", "p1", e1);
        Participant p2 = new Participant("p2", "p2", e1);
        e1.addParticipant(p1);
        e1.addParticipant(p2);
        Expense ex1 = new Expense(e1, p1, new ArrayList<>(List.of(p1, p2)), "ex1", 1.0, "EUR",
                date, true, null);
        Transaction t1 = new Transaction(p1, p2, 1.0, "EUR", date);
        p1.addExpenseInvolved(ex1);
        p2.addExpenseInvolved(ex1);
        p1.addTransactionFrom(t1);
        p2.addTransactionTo(t1);
        p1.addExpensePaid(ex1);
        e1.addExpense(ex1);

        assertEquals(e1, controller.add(e1).getBody());
        assertTrue(participantRepo.getReferenceById(p1.getId()).getExpensesInvolved().contains(ex1));
        assertTrue(participantRepo.getReferenceById(p2.getId()).getExpensesInvolved().contains(ex1));
        assertTrue(participantRepo.getReferenceById(p1.getId()).getExpensesPaid().contains(ex1));
        assertTrue(participantRepo.getReferenceById(p1.getId()).getTransactionsFrom().contains(t1));
        assertTrue(participantRepo.getReferenceById(p2.getId()).getTransactionsTo().contains(t1));
        assertTrue(expenseRepo.getReferenceById(ex1.getId()).getParticipants().contains(p1));
        assertTrue(expenseRepo.getReferenceById(ex1.getId()).getParticipants().contains(p2));
        assertEquals(expenseRepo.getReferenceById(ex1.getId()).getPayer(), p1);
    }

    @Test
    public void getUpdatesNoUpdateTest() {
        DeferredResult<ResponseEntity<Event>> result = controller.getUpdates();
        assertFalse(result.isSetOrExpired());
    }

    @Test
    public void getUpdatesUpdateTest() {
        DeferredResult<ResponseEntity<Event>> result = controller.getUpdates();
        Event e1 = getEvent("e1");
        controller.add(e1);
        assertTrue(result.isSetOrExpired());
        assertEquals(e1, ((ResponseEntity<Event>) result.getResult()).getBody());
    }

    private Event getEvent(String e) {
        Event ret = new Event(e, e, dateTime);
        ret.setInviteCode(Event.generateInviteCode());
        return ret;
    }
}
