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
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class ManageExpenseCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @Mock
    private Event event;

    @Mock
    private Expense expense;

    @InjectMocks
    private ManageExpenseCtrl controller;

    private Scene sceneVerification;

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
        Mockito.reset(server, mainCtrl, event, expense);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<ManageExpenseCtrl, Parent> manageExpense = FXML.load(ManageExpenseCtrl.class,
                "client", "scenes", "ManageExpense.fxml");
        this.controller = manageExpense.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(manageExpense.getValue());
        stage.setScene(scene);
        this.sceneVerification = scene;
        stage.show();
        scene.setOnKeyPressed(e -> controller.keyPressed(e));
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void clearFieldsTest() {
        clickOn("#amount");
        write("10.00");
        verifyThat("10.00", NodeMatchers.isVisible());
        Platform.runLater(() -> controller.clearFields());
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(controller.getAmount().getText().isEmpty());
    }

    @Test
    public void participantRepresentationTest() {
        assertEquals("name (email)", controller.participantRepresentation(
                new Participant("name", "email",
                        Mockito.mock(Event.class))));
    }

    @Test
    public void sceneVerificationTest() {
        assertEquals(sceneVerification, controller.getScene());
    }

    @Test
    public void addNewExpenseTypeNoExceptionTest() {
        clickOn("#newExpenseType");
        write("newtype");
        clickOn("#add");
        verifyThat("newtype", NodeMatchers.isVisible());
        assertTrue(controller.getTypes().containsKey("newtype"));
    }

    @Test
    public void addNewExpenseTypeExceptionTest() {
        clickOn("#newExpenseType");
        write("new type");
        clickOn("#add");
        assertEquals(1, controller.getTypes().size());
        clickOn("#newExpenseType");
        write("new type");
        clickOn("#add");
        assertEquals(1, controller.getTypes().size());
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void showChosenParticipantsTest() {
        Mockito.when(expense.isSplitEqually()).thenReturn(true);
        Platform.runLater(() -> controller.clearFields());
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(0, controller.getParticipantsGroup().getChildren().size());
        Participant participant = new Participant("name", "email", event);
        Mockito.when(event.getParticipants()).thenReturn(new ArrayList<>(
                List.of(participant)));
        Mockito.when(expense.getParticipants()).thenReturn(new ArrayList<>(
                List.of(participant)));
        Mockito.when(expense.isSplitEqually()).thenReturn(true);
        assertFalse(controller.getParticipantsGroup().isVisible());
        clickOn("#onlySome");
        assertTrue(controller.getParticipantsGroup().isVisible());
        verifyThat("name (email)", NodeMatchers.isVisible());
        assertEquals(1, controller.getParticipantsGroup().getChildren().size());
        assertTrue(controller.getChosenParticipants().contains(participant));
        clickOn(controller.getParticipantsGroup().getChildren().getFirst());
        assertFalse(controller.getChosenParticipants().contains(participant));
        clickOn(controller.getParticipantsGroup().getChildren().getFirst());
        assertTrue(controller.getChosenParticipants().contains(participant));
    }

    @Test
    public void showSplitEquallyTest() {
        clickOn("#onlySome");
        assertTrue(controller.getParticipantsGroup().isVisible());
        controller.getParticipantsGroup().setVisible(true);
        clickOn("#equally");
        assertFalse(controller.getParticipantsGroup().isVisible());
        assertTrue(controller.getEqually().isSelected());
        assertFalse(controller.getOnlySome().isSelected());
    }

    @Test
    public void editExpenseTypeTest() {
        clickOn("#newExpenseType");
        write("newtype");
        clickOn("#add");
        HBox hBox = lookup("#expenseTypeContainer").query();
        clickOn(hBox.getChildren().get(0));
        verifyThat("OK", NodeMatchers.isVisible());
    }


    @Test
    public void cancelTest() {
        clickOn("#back");
        Mockito.verify(mainCtrl).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void refreshOverloadExpenseNotNullNotEquallyTest() {
        Participant participant = Mockito.mock(Participant.class);
        Map<String, String> types = Map.of("type1", "#000000", "type2", "#000000");
        Expense expense = new Expense(event, participant,
                List.of(participant), "type", 0.0,
                "currency", LocalDate.now(),
                false, types);
        Mockito.when(event.getExpenses()).thenReturn(new ArrayList<>(List.of(expense)));
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(participant.getName()).thenReturn("name");
        controller.setExpense(expense);
        Platform.runLater(() -> {
            controller.clearFields();
        });
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            controller.refresh(event, expense);
            assertFalse(controller.getEqually().isSelected());
            assertTrue(controller.getOnlySome().isSelected());
            assertEquals(types, controller.getTypes());
            verifyThat("type1", NodeMatchers.isVisible());
            verifyThat("type2", NodeMatchers.isVisible());
        });
    }

    @Test
    public void refreshOverloadExpenseNull() {
        controller.getTypes().put("type", null);
        assertEquals(event, controller.getEvent());
        controller.setExpense(null);
        Event event = new Event();
        Platform.runLater(() -> {
            controller.clearFields();
            controller.refresh(event, expense);
            assertEquals(event, controller.getEvent());
            assertFalse(controller.getTypes().containsKey("type"));
        });
    }

    @Test
    public void updateExpenseTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);
        SingleSelectionModel<String> selectionModel = Mockito.mock(SingleSelectionModel.class);
        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));
        Mockito.when(participant.getExpensesInvolved()).thenReturn(new ArrayList<>());
        Mockito.when(participant.getExpensesPaid()).thenReturn(new ArrayList<>());
        Mockito.when(participant.getEmail()).thenReturn("email");
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(comboBox.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(selectionModel.getSelectedIndex()).thenReturn(0);
        controller.getAmount().setText("1");
        clickOn("#save");
        Mockito.verify(event).updateExpense(Mockito.any(Expense.class));
        Mockito.verify(server).send(Mockito.anyString(), eq(event));
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void addExpenseTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);
        SingleSelectionModel<String> selectionModel = Mockito.mock(SingleSelectionModel.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("1");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));
        Mockito.when(participant.getEmail()).thenReturn("email");
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(comboBox.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(selectionModel.getSelectedIndex()).thenReturn(0);

        clickOn("#save");
        Mockito.verify(event).addExpense(Mockito.any(Expense.class));
        Mockito.verify(server).send(Mockito.anyString(), eq(event));
        Mockito.verify(event).setParticipants(List.of(participant));
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void addExpenseNotANumberExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("K");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));

        clickOn("#save");
        verifyThat("Putting something that is not a number as an expense amount.", NodeMatchers.isVisible());
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void addExpenseNegativeNumberExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("-1");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));

        clickOn("#save");
        verifyThat("The number cannot be zero or negative.", NodeMatchers.isVisible());
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void addExpenseThreeDecimalPlacesExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("2.034");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));

        clickOn("#save");
        verifyThat("The number cannot have more than two decimal places.", NodeMatchers.isVisible());
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void addExpenseNoTitleExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);
        SingleSelectionModel<String> selectionModel = Mockito.mock(SingleSelectionModel.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("1");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));
        Mockito.when(participant.getEmail()).thenReturn("email");
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(comboBox.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(selectionModel.getSelectedIndex()).thenReturn(0);

        clickOn("#save");
        verifyThat("OK", NodeMatchers.isVisible());
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void addExpenseWebApplicationExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        ComboBox<String> comboBox = Mockito.mock(ComboBox.class);
        SingleSelectionModel<String> selectionModel = Mockito.mock(SingleSelectionModel.class);

        controller.setPayer(comboBox);
        controller.getTitle().setText("title");
        controller.getChosenParticipants().add(participant);
        controller.getPayerNames().add("name");
        controller.getPayerEmails().add("email");
        controller.getAmount().setText("1");
        controller.setExpense(null);
        Mockito.when(expense.getId()).thenReturn(1L);
        Mockito.when(event.getParticipants()).thenReturn(List.of(participant));
        Mockito.when(participant.getEmail()).thenReturn("email");
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(comboBox.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(selectionModel.getSelectedIndex()).thenReturn(0);
        Mockito.doThrow(new WebApplicationException())
                .when(server).send(Mockito.anyString(), eq(event));

        clickOn("#save");
        Mockito.verify(server).send(Mockito.anyString(), eq(event));
        verifyThat("OK", NodeMatchers.isVisible());
        Mockito.verify(mainCtrl, Mockito.never()).showEventOverview(Mockito.any(Event.class));
    }

    @Test
    public void addExpenseTypeSuccessfulTest() {
        clickOn("#expenseType");
        type(KeyCode.DOWN);
        verifyThat("food", NodeMatchers.isVisible());
        type(KeyCode.DOWN);
        verifyThat("entrance fees", NodeMatchers.isVisible());
        type(KeyCode.DOWN);
        verifyThat("travel", NodeMatchers.isVisible());
    }

    @Test
    public void addExpenseTypeErrorTest() {
        clickOn("#expenseType");
        type(KeyCode.DOWN);
        verifyThat("food", NodeMatchers.isVisible());
        clickOn("#newExpenseType");
        write("food");
        clickOn("#add");
        verifyThat("Adding the same expense type more than once.", NodeMatchers.isEnabled());
    }

    @Test
    public void getOnlySomeTest() {
        assertFalse(controller.getOnlySome().isSelected());
        clickOn("#onlySome");
        assertTrue(controller.getOnlySome().isSelected());
        assertFalse(controller.getEqually().isSelected());
    }

    @Test
    public void deleteTest() {
        Participant participant = Mockito.mock(Participant.class);
        Mockito.when(event.getParticipants())
                .thenReturn(new ArrayList<>(List.of(participant)));
        Mockito.when(participant.getExpensesInvolved()).thenReturn(new ArrayList<>(List.of(expense)));
        Mockito.when(participant.getExpensesPaid()).thenReturn(new ArrayList<>(List.of(expense)));
        assertTrue(participant.getExpensesInvolved().contains(expense));
        assertTrue(participant.getExpensesPaid().contains(expense));
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(participant.getName()).thenReturn("name");
        clickOn("#delete");

        Mockito.verify(event).updateFields();
        Mockito.verify(server).send(Mockito.anyString(), eq(event));
        Mockito.verify(mainCtrl).showEventOverview(event);
        assertFalse(participant.getExpensesInvolved().contains(expense));
        assertFalse(participant.getExpensesPaid().contains(expense));
    }

    @Test
    public void tooManyLabelsTest() {
        for (int i = 0; i < 6; i++) {
            clickOn("#newExpenseType");
            write("newtype " + i);
            clickOn("#add");
        }
        clickOn("#newExpenseType");
        write("newtype 10");
        clickOn("#add");
        verifyThat("Adding more than 6 tags per expense.", NodeMatchers.isVisible());
    }

    @Test
    public void editingExpenseTypeBeforeSaveTest() {
        controller.setExpense(null);
        clickOn("#newExpenseType");
        write("newtype");
        clickOn("#add");
        HBox hBox = lookup("#expenseTypeContainer").query();
        clickOn(hBox.getChildren().get(0));
        verifyThat("Editing expense types before saving the expense",
                NodeMatchers.isVisible());
    }

    @Test
    public void deleteExpenseType() {
        clickOn("#newExpenseType");
        write("newtype");
        clickOn("#add");
        assertTrue(controller.getTypes().containsKey("newtype"));
        HBox hBox = lookup("#expenseTypeContainer").query();
        clickOn(hBox.getChildren().get(1));
        assertFalse(controller.getTypes().containsKey("newtype"));
    }

    @Test
    public void enterKeyEventTest() {
        push(KeyCode.ENTER);
        Mockito.verify(expense).getId();
    }

    @Test
    public void escKeyEventTest() {
        push(KeyCode.ESCAPE);
        Mockito.verify(mainCtrl).showEventOverview(event);
    }


    @Test
    public void connectWebSocketUpdateTest() throws InterruptedException {
        Mockito.when(event.getParticipants()).thenReturn(new ArrayList<>());
        Mockito.when(event.getTitle()).thenReturn("Event1");
        Mockito.when(event.getInviteCode()).thenReturn("1234");

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/update"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
        Mockito.verify(event).updateFields();
        waitForRunLater();
    }

    @Test
    public void connectWebSocketImportTest() throws InterruptedException {
        Mockito.when(event.getParticipants()).thenReturn(new ArrayList<>());
        Mockito.when(event.getTitle()).thenReturn("Event1");
        Mockito.when(event.getInviteCode()).thenReturn("1234");

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/import"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
        Mockito.verify(event).updateFields();
        waitForRunLater();
    }

    @Test
    public void connectWebSocketDeleteTest() throws InterruptedException {
        Stage stage = Mockito.mock(Stage.class);

        Mockito.when(event.getParticipants()).thenReturn(new ArrayList<>());
        Mockito.when(event.getTitle()).thenReturn("Event1");
        Mockito.when(event.getInviteCode()).thenReturn("1234");
        Mockito.when(mainCtrl.getPrimaryStage()).thenReturn(stage);
        Mockito.when(stage.getScene()).thenReturn(controller.getScene());

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectWebSocket();
        Mockito.verify(server).registerForUpdatesWS(eq("/topic/events/delete"),
                eq(Event.class), captor.capture());
        captor.getValue().accept(event);
        waitForRunLater();
        verifyThat("OK", NodeMatchers.isVisible());
        clickOn("OK");
        Mockito.verify(mainCtrl).showStartScreen();

    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}