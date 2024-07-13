package server.api;

import commons.Event;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.services.EventService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    /**
     * Constructor for the eventController
     *
     * @param eventService the eventService instance used to handle business logic
     */
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Retrieves all events
     *
     * @return a list of all events
     */
    @GetMapping(path = {"", "/"})
    public List<Event> getAll() {
        return eventService.getAll();
    }

    private final Map<Object, Consumer<Event>> listeners = new HashMap<>();

    /**
     * Gets updates to the database (only POST and PUT).
     *
     * @return a deferred result, method execution halted till timeout.
     */
    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Event>> getUpdates() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var result = new DeferredResult<ResponseEntity<Event>>(5000L, noContent);
        var key = new Object();
        listeners.put(key, event -> result.setResult(ResponseEntity.ok(event)));
        result.onCompletion(() -> listeners.remove(key));
        return result;
    }

    /**
     * Retrieves an event by its invite code.
     *
     * @param inviteCode the invite code of the event to retrieve
     * @return a ResponseEntity containing the retrieved event if found
     */
    @GetMapping("/{invite-code}")
    public ResponseEntity<Event> getById(@PathVariable("invite-code") String inviteCode) {
        var event = eventService.getById(inviteCode);
        if (event == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(event);
        }
    }

    /**
     * Adds an event to the database.
     *
     * @param event the event to be added to the database
     * @return the ResponseEntity giving information about the success of the request
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Event> add(@RequestBody Event event) {
        var saved = eventService.add(event);
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        } else {
            new HashMap<>(listeners).forEach((k, l) -> l.accept(event));
            return ResponseEntity.ok(saved);
        }
    }

    /**
     * Replaces an event instance with a new one.
     *
     * @param inviteCode the invite code of the event that should be replaced
     * @param newEvent   the event to replace the existing event
     * @return the ResponseEntity giving information about the success of the request
     */
    @PutMapping("/{invite-code}")
    public ResponseEntity<Event> update(@PathVariable("invite-code") String inviteCode,
                                        @RequestBody Event newEvent) {
        var updated = eventService.update(newEvent);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        } else {
            new HashMap<>(listeners).forEach((k, l) -> l.accept(newEvent));
            return ResponseEntity.ok(updated);
        }
    }

    /**
     * Deletes an event by its invite code.
     *
     * @param inviteCode the invite code of the event to delete.
     * @return a ResponseEntity containing the delete event if found.
     */
    @DeleteMapping(path = "/{invite-code}")
    public ResponseEntity<Event> delete(@PathVariable("invite-code") String inviteCode) {
        var deleted = eventService.delete(inviteCode);
        if (deleted == null) {
            return ResponseEntity.notFound().build();
        } else {
            new HashMap<>(listeners).forEach((k, l) -> l.accept(deleted));
            return ResponseEntity.ok(deleted);
        }
    }

    /**
     * Imports an event to the database.
     *
     * @param event the event to import
     * @return the ResponseEntity containing the imported event
     */
    @Transactional
    public ResponseEntity<Event> importEvent(Event event) {
        var imported = eventService.importEvent(event);
        if (imported == null) {
            return ResponseEntity.badRequest().build();
        } else {
            new HashMap<>(listeners).forEach((k, l) -> l.accept(imported));
            return ResponseEntity.ok(imported);
        }
    }


    /**
     * Adds an event to the database using websockets.
     *
     * @param event the event to add
     * @return the event added
     */
    @MessageMapping("/events/add")
    @SendTo("/topic/events/add")
    public Event addWebSocket(Event event) {
        return add(event).getBody();
    }

    /**
     * Updates an event using websockets.
     *
     * @param event the event to update
     * @return the event updated
     */
    @MessageMapping("/events/update")
    @SendTo("/topic/events/update")
    public Event updateWebSocket(Event event) {
        return update(event.getInviteCode(), event).getBody();
    }

    /**
     * Deletes an event using websockets.
     *
     * @param event the event to delete
     * @return the event deleted
     */
    @MessageMapping("/events/delete")
    @SendTo("/topic/events/delete")
    public Event deleteWebSocket(Event event) {
        return delete(event.getInviteCode()).getBody();
    }

    /**
     * Imports an event using websockets.
     *
     * @param event the event to import
     * @return the imported event
     */
    @MessageMapping("/events/import")
    @SendTo("/topic/events/import")
    public Event importWebSocket(Event event) {
        return importEvent(event).getBody();
    }

}
