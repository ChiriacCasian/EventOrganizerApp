package client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.api.EventController;
import server.services.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EventController.class)
class EventRequestTest {

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @MockBean
    private EventService service;

    private ServerUtils serverUtils;

    @Mock
    private Client mockClient;

    @Mock
    private WebTarget mockWebTarget = Mockito.mock(WebTarget.class);

    @Mock
    private Invocation.Builder mockBuilder = Mockito.mock(Invocation.Builder.class);

    @Mock
    private Response mockResponse = Mockito.mock(Response.class);

    private Event event;


    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new EventController(service)).build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.serverUtils = new ServerUtils();
        this.serverUtils.setClient(mockClient);

        when(mockClient.target(Mockito.anyString()))
                .thenReturn(mockWebTarget);
        when(mockWebTarget.path(Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.queryParam(Mockito.anyString(), Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.accept(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        this.event = new Event("title", "description", LocalDateTime.now());
    }


    @Test
    public void addEventTest() throws Exception {
        when(service.add(event)).thenReturn(event);

        MvcResult result = this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read = mapper.readValue(content, Event.class);

        when(mockBuilder.post(Entity.entity(event, APPLICATION_JSON), Event.class))
                .thenReturn(event);
        assertEquals(read, serverUtils.addEvent(event));
    }


    @Test
    public void addEventBadRequest() throws Exception {
        event = null;

        when(service.add(event)).thenReturn(event);

        MvcResult result = this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read;
        if (content.isEmpty()) {
            read = null;
        } else {
            read = mapper.readValue(content, Event.class);
        }
        when(mockBuilder.post(Entity.entity(event, APPLICATION_JSON), Event.class))
                .thenReturn(event);
        assertEquals(read, serverUtils.addEvent(event));
    }

    @Test
    public void getEventTest() throws Exception {
        when(service.getById(event.getInviteCode())).thenReturn(event);

        MvcResult result = this.mockMvc.perform(get("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read = mapper.readValue(content, Event.class);
        when(mockBuilder.get(new GenericType<Event>() {
        }))
                .thenReturn(event);
        assertEquals(read, serverUtils.getEvent(event.getInviteCode()));
    }

    @Test
    public void getEventDoesNotExistTest() throws Exception {
        when(service.getById(event.getInviteCode())).thenReturn(null);

        MvcResult result = this.mockMvc.perform(get("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isNotFound())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read;
        if (content.isEmpty()) {
            read = null;
        } else {
            read = mapper.readValue(content, Event.class);
        }

        when(mockBuilder.get(new GenericType<Event>() {
        }))
                .thenReturn(null);
        assertEquals(read, serverUtils.getEvent(null));
    }

    @Test
    public void updateEventTest() throws Exception {
        Event updatedEvent = new Event("new title", "new description", LocalDateTime.now());
        updatedEvent.setInviteCode(event.getInviteCode());
        when(service.update(updatedEvent)).thenReturn(updatedEvent);

        MvcResult result = this.mockMvc.perform(put("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read = mapper.readValue(content, Event.class);

        when(mockBuilder.put(Entity.entity(updatedEvent, APPLICATION_JSON), Event.class))
                .thenReturn(updatedEvent);
        assertEquals(read, serverUtils.updateEvent(event.getInviteCode(), updatedEvent));
    }

    @Test
    public void updateEventDoesNotExistTest() throws Exception {
        Event updatedEvent = new Event("new title", "new description", LocalDateTime.now());
        when(service.update(updatedEvent)).thenReturn(null);

        MvcResult result = this.mockMvc.perform(put("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read;
        if (content.isEmpty()) {
            read = null;
        } else {
            read = mapper.readValue(content, Event.class);
        }

        when(mockBuilder.put(Entity.entity(updatedEvent, APPLICATION_JSON), Event.class))
                .thenReturn(null);
        assertEquals(read, serverUtils.updateEvent(event.getInviteCode(), updatedEvent));
    }


    @Test
    public void deleteTest() throws Exception {
        when(service.delete(event.getInviteCode())).thenReturn(event);

        MvcResult result = this.mockMvc.perform(delete("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read = mapper.readValue(content, Event.class);

        when(mockBuilder.delete(new GenericType<Event>() {
        }))
                .thenReturn(event);
        assertEquals(read, serverUtils.deleteEvent(event.getInviteCode()));
    }

    @Test
    public void deleteNotFoundTest() throws Exception {
        when(service.delete(event.getInviteCode())).thenReturn(null);

        MvcResult result = this.mockMvc.perform(delete("/api/events/" + event.getInviteCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isNotFound())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Event read;
        if (content.isEmpty()) {
            read = null;
        } else {
            read = mapper.readValue(content, Event.class);
        }

        when(mockBuilder.delete(new GenericType<Event>() {
        }))
                .thenReturn(null);
        assertEquals(read, serverUtils.deleteEvent(event.getInviteCode()));
    }

    @Test
    public void getAllEvents() throws Exception {
        List<Event> events = new ArrayList<>(List.of(event, event));
        when(service.getAll()).thenReturn(new ArrayList<>(List.of(event, event)));
        MvcResult result = this.mockMvc.perform(get("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(events)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        List<Event> read = mapper.readValue(content, new TypeReference<>() {
        });

        when(mockBuilder.get(new GenericType<List<Event>>() {
        })).thenReturn(events);

        assertEquals(read, serverUtils.getEvents());
    }
}