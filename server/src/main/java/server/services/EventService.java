package server.services;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Constructor for an event service.
     *
     * @param eventRepository       the event repository to use.
     * @param participantRepository the participant repository to use.
     * @param expenseRepository     the expense repository to use.
     */
    public EventService(EventRepository eventRepository,
                        ParticipantRepository participantRepository,
                        ExpenseRepository expenseRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Gets all events from the repository.
     *
     * @return a list of all events.
     */
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    /**
     * Checks if an event exists.
     *
     * @param inviteCode the invite code of the event.
     * @return true if it exists, false otherwise.
     */
    public boolean exists(String inviteCode) {
        return eventRepository.existsById(inviteCode);
    }

    /**
     * Gets an event by its id.
     *
     * @param inviteCode the invite code of the event.
     * @return the event instance if it exists, null otherwise.
     */
    public Event getById(String inviteCode) {
        Event ret = eventRepository.findById(inviteCode).orElse(null);
        if (ret != null) ret.updateFields();
        return ret;
    }

    /**
     * Adds an event to the repository if it is valid.
     *
     * @param event the event to add.
     * @return the event if adding was successful, null otherwise.
     */
    @Transactional
    public Event add(Event event) {
        if (isInvalid(event)) {
            return null;
        } else {
            while (exists(event.getInviteCode())) {
                event.setInviteCode(Event.generateInviteCode());
            }
            event.updateFields();
            List<Transaction> transactions = event.getParticipants().stream()
                    .flatMap(participant -> participant.getTransactionsFrom().stream())
                    .toList();
            Event prelim = new Event();
            prelim.setInviteCode(event.getInviteCode());
            eventRepository.save(prelim);
            addHelper(event);
            expenseRepository.saveAll(event.getExpenses());
            for (Expense expense : event.getExpenses()) {
                expense.getPayer().addExpensePaid(expense);
                for (Participant participant : expense.getParticipants()) {
                    participant.addExpenseInvolved(expense);
                }
            }
            for (Transaction transaction : transactions) {
                transaction.getPayer().addTransactionFrom(transaction);
                transaction.getPayee().addTransactionTo(transaction);
            }
            participantRepository.saveAll(event.getParticipants());
            return eventRepository.save(event);
        }
    }

    /**
     * Adds participants to the repository without expenses to prevent
     * foreign key constraints violations.
     *
     * @param event the event to get the participants from.
     */
    @Transactional
    public void addHelper(Event event) {
        participantRepository.saveAll(event.getParticipants().stream()
                .peek(participant -> {
                    participant.setExpensesPaid(new ArrayList<>());
                    participant.setExpensesInvolved(new ArrayList<>());
                    participant.setTransactionsFrom(new ArrayList<>());
                    participant.setTransactionsTo(new ArrayList<>());
                }).toList());
    }


    /**
     * Updates an event in the repository if the updated event is valid.
     *
     * @param event the event to replace the old event with.
     * @return the updated event if updating was successful, null otherwise.
     */
    @Transactional
    public Event update(Event event) {
        if (isInvalid(event)) {
            return null;
        } else {
            event.updateFields();
            return eventRepository.save(event);
        }
    }


    /**
     * Deletes an event from the repository if it exists.
     *
     * @param inviteCode the invite code of the event.
     * @return the event if deleting was successful, null otherwise.
     */
    @Transactional
    public Event delete(String inviteCode) {
        if (!exists(inviteCode)) {
            return null;
        } else {
            Event deleted = getById(inviteCode);
            eventRepository.delete(deleted);
            return deleted;
        }
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private static boolean isInvalid(Event event) {
        return isNullOrEmpty(event.getTitle()) ||
                !Event.validateInviteCode(event.getInviteCode()) ||
                event.getDateOfCreation() == null;
    }

    /**
     * Imports an event to the database.
     *
     * @param event the event to import
     * @return the imported event
     */
    @Transactional
    public Event importEvent(Event event) {
        delete(event.getInviteCode());
        return add(event);
    }
}
