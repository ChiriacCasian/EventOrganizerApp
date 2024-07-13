package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Debt;
import commons.Event;
import commons.Participant;
import commons.Transaction;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
public class SettleDebtTest extends ApplicationTest {

    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @Mock
    private Debt debt;

    @Mock
    private Transaction transaction;

    @Mock
    private Participant participant;

    @Mock
    private Event event;

    private Scene sceneVerification;

    @InjectMocks
    private SettleDebtCtrl controller;

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
        Mockito.reset(server, mainCtrl, debt, transaction, participant, event);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<SettleDebtCtrl, Parent> settleDebt = FXML.load(SettleDebtCtrl.class,
                "client", "scenes", "SettleDebt.fxml");
        this.controller = settleDebt.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(settleDebt.getValue());
        sceneVerification = scene;
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void refreshTest() {
        assertDoesNotThrow(() -> controller.refresh());
    }

    @Test
    public void setDebtTest() {
        assertEquals(controller.getDebt(), debt);
    }

    @Test
    public void setEventTest() {
        Event event = new Event();
        controller.setEvent(event);
        assertEquals(controller.getEvent(), event);
        assertTrue(controller.isActive());
    }

    @Test
    public void closeTest() {
        assertTrue(controller.getAmount().getScene().getWindow().isShowing());
        controller.setValue(1.0);
        assertEquals(controller.getValue(), 1.0);
        Button cancel = lookup("#cancel").queryButton();
        clickOn(cancel);
        assertFalse(controller.getAmount().getScene().getWindow().isShowing());
        assertEquals(controller.getValue(), 0.0);
        assertFalse(controller.isActive());
    }

    @Test
    public void confirmTest() {
        Mockito.when(debt.createTransaction(Mockito.anyDouble())).thenReturn(transaction);
        doReturn(participant).when(transaction).getPayer();
        doReturn(participant).when(transaction).getPayee();

        doReturn(true).when(participant).addTransactionFrom(transaction);
        doReturn(true).when(participant).addTransactionTo(transaction);

        Button cancel = lookup("#confirm").queryButton();
        clickOn(cancel);
        assertTrue(mockingDetails(event).isMock());

        Mockito.verify(debt).createTransaction(Mockito.anyDouble());
        Mockito.verify(transaction.getPayer()).addTransactionFrom(transaction);
        Mockito.verify(transaction.getPayee()).addTransactionTo(transaction);
        Mockito.verify(server).send("/app/events/update", event);
        assertFalse(controller.isActive());
    }

    @Test
    public void closeOnDebtChangeTest() throws InterruptedException {
        assertTrue(controller.isActive());
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.closeOnDebtChange();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        assertFalse(controller.isActive());
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void getSceneTest() {
        assertEquals(sceneVerification, controller.getScene());
    }

    @Test
    public void connectWebSocketUpdateTest() {
        Set<Debt> debts = Mockito.mock(Set.class);
        Mockito.doNothing().when(event).updateFields();
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        doReturn(debts).when(event).debtOverview();
        doReturn(false).when(debts).contains(debt);
        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/update"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
    }

    @Test
    public void connectWebSocketImportTest() {
        Set<Debt> debts = Mockito.mock(Set.class);
        Mockito.doNothing().when(event).updateFields();
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        doReturn(debts).when(event).debtOverview();
        doReturn(false).when(debts).contains(debt);
        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/import"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
    }

    @Test
    public void connectWebSocketDeleteTest() throws InterruptedException {
        Stage stage = Mockito.mock(Stage.class);
        Mockito.doNothing().when(event).updateFields();
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        doReturn(stage).when(mainCtrl).getPrimaryStage();
        doReturn(sceneVerification).when(stage).getScene();
        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/delete"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
        waitForRunLater();
        Mockito.verify(mainCtrl).showStartScreen();
        verifyThat("OK", NodeMatchers.isVisible());
    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}

