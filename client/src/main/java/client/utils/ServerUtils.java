/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.utils;

import client.Main;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.AdminUser;
import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import javafx.application.Platform;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
    protected static final String SERVER = "http://" +
            System.getProperty("server.host") + "/";

    protected static Client client = ClientBuilder.newClient(new ClientConfig());


    /**
     * Gets the client
     *
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the client
     *
     * @param newClient the new client
     */
    public void setClient(Client newClient) {
        client = newClient;
    }

    // * Admin Methods

    /**
     * Adds a user to the repository
     *
     * @param user to be added
     * @return user added
     */
    public AdminUser addUser(AdminUser user) {
        return client //
                .target(SERVER).path("api/admin")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(user, APPLICATION_JSON), AdminUser.class);
    }

    /**
     * Gets a list of passwords available
     *
     * @param password the password
     * @return the list of all the passwords.
     */
    public AdminUser getAdminUser(String password) {
        return client //
                .target(SERVER).path("api/admin/" + password) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<AdminUser>() {
                });
    }

    // * Events Methods

    /**
     * Used to add a new Event to the database
     *
     * @param event the event to add
     * @return the event added
     */
    public Event addEvent(Event event) {
        return client //
                .target(SERVER).path("api/events") //
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * Used to get a specific event based on the id from the database through the Rest api.
     *
     * @param inviteCode is the invite code of the event to get
     * @return the specific event
     */
    public Event getEvent(String inviteCode) {
        return client //
                .target(SERVER).path("api/events/" + inviteCode) //
                .request(APPLICATION_JSON).accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Updates an Event in the database
     *
     * @param inviteCode invite code of the event to update
     * @param newEvent   new event
     * @return the updated event
     */
    public Event updateEvent(String inviteCode, Event newEvent) {
        return client //
                .target(SERVER).path("api/events/" + inviteCode) //
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newEvent, APPLICATION_JSON), Event.class);
    }

    /**
     * Deletes an event from the database
     *
     * @param inviteCode is the invite code of the event to delete
     * @return the deleted event
     */
    public Event deleteEvent(String inviteCode) {
        return client //
                .target(SERVER).path("api/events/" + inviteCode) //
                .request(APPLICATION_JSON).accept(APPLICATION_JSON).delete(
                        new GenericType<>() {
                        });
    }

    /**
     * is used to get all events from the database through the Rest api
     *
     * @return a list of all events
     */
    public List<Event> getEvents() {
        return client //
                .target(SERVER).path("/api/events") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Event>>() {
                });
    }

    // * WebSocket Methods

    private StompSession session;

    /**
     * Sets the session
     *
     * @param session the session
     */
    public void setSession(StompSession session) {
        this.session = session;
    }

    /**
     * Connects to the websocket
     *
     * @param url the url
     * @return the stomp session
     */
    public StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stomp.setMessageConverter(converter);
        try {
            return stomp.connectAsync(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (ExecutionException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new IllegalStateException();
    }

    /**
     * Registers for updates to a generic using websockets.
     *
     * @param dest     the destination URI
     * @param type     the type of the generic
     * @param consumer the consumer to use with the generic
     * @param <T>      the entity type
     */
    public <T> void registerForUpdatesWS(String dest, Class<T> type, Consumer<T> consumer) {
        if (session == null || !session.isConnected()) {
            try {
                session = connect("ws://" + System.getProperty("server.host") + "/websocket");
            } catch (Exception e) {
                Platform.runLater(ErrorConnectionAlert::showAlert);
                return;
            }
        }
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    /**
     * Sends an object using websockets.
     *
     * @param destination the destination uri.
     * @param o           the object to send.
     */
    public void send(String destination, Object o) {
        if (session == null || !session.isConnected()) {
            try {
                session = connect("ws://" + System.getProperty("server.host") + "/websocket");
                Main.connect();
            } catch (Exception e) {
                Platform.runLater(ErrorConnectionAlert::showAlert);
                return;
            }
        }
        session.send(destination, o);
    }

    // * Long Polling Methods

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Gets the executor service
     *
     * @return the executor service
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Sets the executor service
     *
     * @param newExecutor the new executor service
     */
    public void setExecutor(ExecutorService newExecutor) {
        executor = newExecutor;
    }

    /**
     * Register for updates to the event database (only POST and PUT).
     *
     * @param consumer the consumer used to register updates.
     */
    public void registerForUpdates(Consumer<Event> consumer) {
        executor.submit(() -> {
            while (!Thread.interrupted()) {
                var response = client //
                        .target(SERVER).path("api/events/updates") //
                        .request(APPLICATION_JSON) //
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if (response.getStatus() == 204) {
                    continue;
                }
                try {
                    var event = response.readEntity(Event.class);
                    consumer.accept(event);
                } catch (Exception e) {
                    consumer.accept(null);
                }
            }
        });
    }

    /**
     * Stops the executor service.
     */
    public void stop() {
        executor.shutdownNow();
    }

    /**
     * sends mail
     *
     * @param mail address to send to
     * @param sub  inviteCode of Event
     * @param bod  body of the message
     * @return the invite code
     */
    public String sendMail(String mail, String sub, String bod) {
        String from = System.getProperty("from.email");
        String password = System.getProperty("from.password");
        return client
                .target(SERVER)
                .path("/api/mail")
                .queryParam("mail", mail)
                .queryParam("sub", sub)
                .queryParam("bod", bod)
                .queryParam("from", from)
                .queryParam("password", password)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(sub, APPLICATION_JSON), String.class);
    }
}