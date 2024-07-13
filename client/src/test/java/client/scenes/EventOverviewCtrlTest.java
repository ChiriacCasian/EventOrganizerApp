package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventOverviewCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    private Scene sceneVerification;

    private Event event;

    @InjectMocks
    private EventOverviewCtrl controller;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "0");
    }

    @BeforeEach
    public void setup() {
        this.event = new Event("title", "description", LocalDateTime.now());
        event.setCurrency("EUR");
        controller.setEvent(event);
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
        Pair<EventOverviewCtrl, Parent> eventOverviewCtrlParentPair = FXML.load(EventOverviewCtrl.class,
                "client", "scenes", "EventOverview.fxml");
        this.controller = eventOverviewCtrlParentPair.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(eventOverviewCtrlParentPair.getValue());
        stage.setScene(scene);
        stage.show();
        this.sceneVerification = scene;
        scene.setOnKeyPressed(e -> controller.keyPressed(e));
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void setGraphicsTest() {
        assertNull(controller.getEdit().getGraphic());
        Platform.runLater(() -> {
            controller.setGraphics();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(controller.getEdit().getGraphic());
    }

    @Test
    public void getSceneTest() {
        assertEquals(sceneVerification, controller.getScene());
    }


    @Test
    public void goBackTest() {
        press(KeyCode.ESCAPE);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showStartScreen();
    }

    @Test
    public void editTitleTest() {
        clickOn("#edit");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showEditTitle(event);
    }

    @Test
    public void addOrRemoveParticipantTest() {
        clickOn("#addOrRemoveParticipant");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showAddParticipant(event);
    }

    @Test
    public void showExpensesIncludingParticipantsTest() throws InterruptedException {
        Participant p1 = new Participant("name 1", "email 1", event);
        Participant p2 = new Participant("name 2", "email 2", event);
        event.addParticipant(p1);
        event.addParticipant(p2);
        Expense e1 = new Expense(event, p1, event.getParticipants(), "title",
                10.36, "EUR", LocalDate.now(),
                true, null);
        p1.addExpenseInvolved(e1);
        p2.addExpenseInvolved(e1);
        p1.addExpensePaid(e1);
        event.addExpense(e1);

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();


        ChoiceBox<String> participantChoice = lookup("#choseParticipant").query();
        clickOn(participantChoice);
        WaitForAsyncUtils.waitForFxEvents();
        CountDownLatch latch1 = new CountDownLatch(1);

        Platform.runLater(() -> {
            participantChoice.getSelectionModel().select(1);
            WaitForAsyncUtils.waitForFxEvents();
            latch1.countDown();
        });
        latch1.await(5, TimeUnit.SECONDS);

        clickOn("#includingPerson");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Total owed by name 2: 5.18 EUR (100%)", controller.getDebt().getText());
    }

    @Test
    public void showExpensesFromParticipantsTest() throws InterruptedException {
        Participant p1 = new Participant("name 1", "email 1", event);
        Participant p2 = new Participant("name 2", "email 2", event);
        event.addParticipant(p1);
        event.addParticipant(p2);
        Expense e1 = new Expense(event, p1, event.getParticipants(), "title",
                10.36, "EUR", LocalDate.now(),
                true, null);
        p1.addExpenseInvolved(e1);
        p2.addExpenseInvolved(e1);
        p1.addExpensePaid(e1);
        event.addExpense(e1);

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();


        ChoiceBox<String> participantChoice = lookup("#choseParticipant").query();
        clickOn(participantChoice);
        WaitForAsyncUtils.waitForFxEvents();
        CountDownLatch latch1 = new CountDownLatch(1);

        Platform.runLater(() -> {
            participantChoice.getSelectionModel().select(0);
            WaitForAsyncUtils.waitForFxEvents();
            latch1.countDown();
        });
        latch1.await(5, TimeUnit.SECONDS);

        clickOn("#fromPerson");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Total owed to name 1: 5.18 EUR (100%)", controller.getDebt().getText());
    }

    @Test
    public void showAllExpensesTest() throws InterruptedException {
        Participant p1 = new Participant("name 1", "email 1", event);
        Participant p2 = new Participant("name 2", "email 2", event);
        event.addParticipant(p1);
        event.addParticipant(p2);
        Expense e1 = new Expense(event, p1, event.getParticipants(), "title",
                10.36, "EUR", LocalDate.now(),
                true, null);
        p1.addExpenseInvolved(e1);
        p2.addExpenseInvolved(e1);
        p1.addExpensePaid(e1);
        event.addExpense(e1);

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();


        ChoiceBox<String> participantChoice = lookup("#choseParticipant").query();
        clickOn(participantChoice);
        WaitForAsyncUtils.waitForFxEvents();
        CountDownLatch latch1 = new CountDownLatch(1);

        Platform.runLater(() -> {
            participantChoice.getSelectionModel().select(0);
            WaitForAsyncUtils.waitForFxEvents();
            latch1.countDown();
        });
        latch1.await(5, TimeUnit.SECONDS);

        clickOn("#all");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Total sum of expenses: 10.36 EUR", controller.getDebt().getText());
    }

    @Test
    public void clickOnExpense() throws InterruptedException {
        Participant p1 = new Participant("name 1", "email 1", event);
        Participant p2 = new Participant("name 2", "email 2", event);
        event.addParticipant(p1);
        event.addParticipant(p2);
        Expense e1 = new Expense(event, p1, event.getParticipants(), "title",
                10.36, "EUR", LocalDate.now(),
                true, null);
        p1.addExpenseInvolved(e1);
        p2.addExpenseInvolved(e1);
        p1.addExpensePaid(e1);
        event.addExpense(e1);

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();

        CountDownLatch latch1 = new CountDownLatch(1);

        Platform.runLater(() -> {
            controller.getListOfExpenses().getSelectionModel().select(0);
            WaitForAsyncUtils.waitForFxEvents();
            latch1.countDown();
        });

        latch1.await();

        Mockito.verify(mainCtrl).showManageExpense(event, e1);
    }

    @Test
    public void addExpenseTest() {
        clickOn("#addExpense");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl, Mockito.atLeastOnce()).showManageExpense(event, null);
    }

    @Test
    public void settleDebtsTest() {
        clickOn("#settleDebts");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showDebtOverview(event);
    }

    @Test
    public void sendInvitesTest() {
        clickOn("#sendInvites");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showInvitation(event);
    }
}