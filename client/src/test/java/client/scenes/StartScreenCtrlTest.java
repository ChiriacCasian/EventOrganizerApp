package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class StartScreenCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private StartScreenCtrl controller;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "0");
    }

    @AfterEach
    public void resetMocks() {
        Mockito.reset(server, mainCtrl);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<StartScreenCtrl, Parent> startScreen = FXML.load(StartScreenCtrl.class,
                "client", "scenes", "StartScreen.fxml");
        this.controller = startScreen.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(startScreen.getValue());
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void joinEventSuccessfulTest() {
        String eventTitle = "Event1";
        Event event = Mockito.mock(Event.class);
        controller.getToJoinEvent().setText(eventTitle);
        Mockito.when(server.getEvent(eventTitle)).thenReturn(event);
        Mockito.doNothing().when(event).updateFields();

        Button joinEvent = lookup("#joinEvent").query();
        clickOn(joinEvent);

        Mockito.verify(server).getEvent(eventTitle);
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void joinEventFailureEmptyTest() {
        String inviteCode = "";
        controller.getToJoinEvent().setText(inviteCode);
        Mockito.when(server.getEvent(inviteCode)).thenThrow
                (Mockito.mock(WebApplicationException.class));

        Button joinEvent = lookup("#joinEvent").query();
        clickOn(joinEvent);
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void joinEventFailureInvalidTest() {
        String eventTitle = "a";
        controller.getToJoinEvent().setText(eventTitle);
        Mockito.when(server.getEvent(eventTitle)).thenThrow
                (Mockito.mock(WebApplicationException.class));

        Button joinEvent = lookup("#joinEvent").query();
        clickOn(joinEvent);
        verifyThat("OK", NodeMatchers.isVisible());
    }


    @Test
    public void createEventSuccessfulTest() {
        Button createEvent = lookup("#createEvent").query();
        clickOn(createEvent);
        Mockito.verify(server).addEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void createEventFailureTest() {
        Mockito.when(server.addEvent(Mockito.any(Event.class)))
                .thenThrow(Mockito.mock(WebApplicationException.class));
        Button createEvent = lookup("#createEvent").query();
        clickOn(createEvent);
        verifyThat("OK", NodeMatchers.isVisible());
    }


    @Test
    public void joinEventKeyTest() {
        Event event = Mockito.mock(Event.class);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(event);
        Mockito.doNothing().when(event).updateFields();

        clickOn("#toJoinEvent");
        write("Event1");
        press(javafx.scene.input.KeyCode.ENTER);
        Mockito.verify(server).getEvent("Event1");
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void createEventKeyTest() {
        clickOn("#newEvent");
        press(javafx.scene.input.KeyCode.ENTER);

        Mockito.verify(server).addEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void createEventTooLongTest() {
        clickOn("#newEvent");
        write("a".repeat(25));
        press(javafx.scene.input.KeyCode.ENTER);
        Mockito.verify(server, Mockito.never()).addEvent(Mockito.any(Event.class));
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
        verifyThat("Event title too long", NodeMatchers.isVisible());
    }

    @Test
    public void loginTest() {
        clickOn("#toLogin");
        Mockito.verify(mainCtrl).showLogin();
    }

    @Test
    public void refreshNoExceptionTest() {
        Event e1 = Mockito.mock(Event.class);
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(e1);
        Mockito.when(e1.getTitle()).thenReturn("Event1");
        Mockito.when(e1.getInviteCode()).thenReturn("1234");
        controller.refresh();
        Mockito.verify(server).getEvent(Mockito.anyString());
    }

    @Test
    public void refreshExceptionTest() {
        Event e1 = Mockito.mock(Event.class);
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenThrow
                (Mockito.mock(WebApplicationException.class));
        Mockito.when(e1.getInviteCode()).thenReturn("1234");
        controller.refresh();
        Mockito.verify(mainCtrl).removeFromRecentEvents(e1);
    }

    @Test
    public void testChangedMethod() {
        Event e1 = new Event("Event1", "1234", LocalDateTime.now());
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(e1);
        ListView<String> rEvents = controller.getREvents();

        controller.refresh();
        rEvents.getSelectionModel().select(0);
        Mockito.verify(mainCtrl).showEventOverview(e1);
    }


    @Test
    public void connectWebSocketUpdateTest() throws InterruptedException {
        Event e1 = Mockito.mock(Event.class);
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(e1);
        Mockito.when(e1.getTitle()).thenReturn("Event1");
        Mockito.when(e1.getInviteCode()).thenReturn("1234");

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/update"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(Mockito.mock(Event.class));
        waitForRunLater();
        Mockito.verify(server).getEvent(Mockito.anyString());
    }

    @Test
    public void connectWebSocketImportTest() throws InterruptedException {
        Event e1 = Mockito.mock(Event.class);
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(e1);
        Mockito.when(e1.getTitle()).thenReturn("Event1");
        Mockito.when(e1.getInviteCode()).thenReturn("1234");

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/import"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(e1);
        waitForRunLater();
        Mockito.verify(server).getEvent(Mockito.anyString());
    }

    @Test
    public void connectWebSocketDeleteTest() throws InterruptedException {
        Event e1 = Mockito.mock(Event.class);
        List<Event> recent = new ArrayList<>(List.of(e1));
        Mockito.when(mainCtrl.recentEventsSublist()).thenReturn(recent);
        Mockito.when(server.getEvent(Mockito.anyString())).thenReturn(e1);
        Mockito.when(e1.getTitle()).thenReturn("Event1");
        Mockito.when(e1.getInviteCode()).thenReturn("1234");

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/delete"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(e1);

        Mockito.verify(mainCtrl).removeFromRecentEvents(e1);
        waitForRunLater();
        Mockito.verify(server).getEvent(Mockito.anyString());
    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    // * Language Switch Tests

    @Test
    public void languageSwitchTest() {
        Platform.runLater(() -> controller.setFlags());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.doAnswer(invocation -> {
            controller.setLanguage(Locale.of("en"));
            return null;
        }).when(mainCtrl).updateLanguage(Locale.of("en"));

        clickOn("#languageIndicator");
        clickOn("#english");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(Locale.ENGLISH, Language.getLanguage());

        Mockito.doAnswer(invocation -> {
            controller.setLanguage(Locale.of("nl"));
            return null;
        }).when(mainCtrl).updateLanguage(Locale.of("nl"));

        clickOn("#languageIndicator");
        clickOn("#dutch");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(Locale.of("nl"), Language.getLanguage());

        clickOn("#languageIndicator");
        clickOn("#contribute");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("Language template created", NodeMatchers.isVisible());

        Platform.runLater(() -> Language.setLanguage(Locale.ENGLISH));
        WaitForAsyncUtils.waitForFxEvents();
    }

}