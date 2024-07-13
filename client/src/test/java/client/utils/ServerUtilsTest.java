package client.utils;

import commons.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.matcher.base.NodeMatchers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class ServerUtilsTest {

    ServerUtils serverUtils;

    @BeforeAll
    public static void setUpClass() {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {
                });
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    public void setUp() {
        serverUtils = new ServerUtils();
    }

    @Test
    public void testGetClient() {
        assertNotNull(serverUtils.getClient());
    }

    @Test
    public void sendTestNoServer() throws Exception {
        serverUtils.send("/app/events/update", new Event());
        waitForRunLater();
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void sendMockSessionTest() {
        Event event = new Event();
        StompSession session = Mockito.mock(StompSession.class);
        serverUtils.setSession(session);
        when(session.isConnected()).thenReturn(true);
        when(session.send(anyString(), Mockito.any())).thenReturn(null);

        serverUtils.send("/app/events/update", event);

        Mockito.verify(session).send("/app/events/update", event);
    }

    @Test
    public void registerForUpdatesWSTestNoServer() throws Exception {
        serverUtils.registerForUpdatesWS("/topic/events/update", Event.class,
                event -> System.out.println("Event received: " + event));
        waitForRunLater();
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void registerForUpdatesWSSessionMock() {
        StompSession session = Mockito.mock(StompSession.class);
        serverUtils.setSession(session);
        when(session.isConnected()).thenReturn(true);
        when(session.subscribe(anyString(), Mockito.any())).thenReturn(null);

        serverUtils.registerForUpdatesWS("/topic/events/update", Event.class,
                event -> System.out.println("Event received: " + event));

        ArgumentCaptor<StompFrameHandler> argumentCaptor = ArgumentCaptor.forClass(StompFrameHandler.class);
        Mockito.verify(session).subscribe(eq("/topic/events/update"), argumentCaptor.capture());

        StompFrameHandler handler = argumentCaptor.getValue();
        StompHeaders headers = new StompHeaders();
        Event event = new Event();

        assertEquals(Event.class, handler.getPayloadType(headers));

        handler.handleFrame(headers, event);
    }

    @Test
    public void connectTestNoServer() {
        assertThrows(RuntimeException.class, () -> serverUtils.connect("/app/events/update"));
    }

    @Test
    public void registerForUpdatesOKTest() throws InterruptedException {
        Client mockClient = Mockito.mock(Client.class);
        Response mockResponse = Mockito.mock(Response.class);
        Event testEvent = new Event();
        String inviteCode1 = Event.generateInviteCode();
        testEvent.setInviteCode(inviteCode1);
        String inviteCode2 = Event.generateInviteCode();
        while (inviteCode2.equals(inviteCode1)) {
            inviteCode2 = Event.generateInviteCode();
        }

        when(mockClient.target(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString()).get(Response.class)).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Event.class)).thenReturn(testEvent);

        final String newInviteCode = inviteCode2;
        serverUtils.setClient(mockClient);
        CountDownLatch latch = new CountDownLatch(1);
        serverUtils.registerForUpdates(event -> {
            event.setInviteCode(newInviteCode);
            latch.countDown();
        });
        latch.await(1, TimeUnit.SECONDS);
    }

    @Test
    public void registerForUpdatesExceptionTest() throws InterruptedException {
        Client mockClient = Mockito.mock(Client.class);
        Response mockResponse = Mockito.mock(Response.class);
        Event testEvent = new Event();
        String inviteCode1 = Event.generateInviteCode();
        testEvent.setInviteCode(inviteCode1);

        when(mockClient.target(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString()).get(Response.class)).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Event.class)).thenReturn(testEvent);

        final String newInviteCode = "1234";
        serverUtils.setClient(mockClient);
        CountDownLatch latch = new CountDownLatch(1);
        serverUtils.registerForUpdates(event -> {
            try {
                event.setInviteCode(newInviteCode);
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertNotEquals(newInviteCode, testEvent.getInviteCode());
    }

    @Test
    public void registerForUpdatesNoContentTest() throws InterruptedException {
        Client mockClient = Mockito.mock(Client.class);
        Response mockResponse = Mockito.mock(Response.class);

        when(mockClient.target(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString())).thenReturn(Mockito.mock(WebTarget.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString())).thenReturn(Mockito.mock(Invocation.Builder.class));
        when(mockClient.target(anyString()).path(anyString()).request(anyString()).accept(anyString()).get(Response.class)).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(204);

        serverUtils.setClient(mockClient);
        CountDownLatch latch = new CountDownLatch(1);
        serverUtils.registerForUpdates(event -> latch.countDown());
        assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void stopTest() {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        serverUtils.setExecutor(executor);
        assertEquals(executor, ServerUtils.getExecutor());
        Mockito.when(executor.shutdownNow()).thenReturn(null);
        serverUtils.stop();
        Mockito.verify(executor).shutdownNow();
    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}
