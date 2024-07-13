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
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AddParticipantCtrlTest extends ApplicationTest {
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;
    @InjectMocks
    private AddParticipantCtrl controller;

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
        Pair<AddParticipantCtrl, Parent> addParticipant = FXML.load(AddParticipantCtrl.class,
                "client", "scenes", "AddParticipant.fxml");
        this.controller = addParticipant.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(addParticipant.getValue());
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void addParticipantTest() throws CloneNotSupportedException {
        String name = "casian";
        String email = "aa@bb.com";
        String iban = "AD1400080001001234567890";
        String bic = "SMCOGB2L";
        Event event = Mockito.mock(Event.class);
        Participant p1 = new Participant(name, email, event, iban, bic);
        controller.setEvent(event);
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);

        Button okButton = lookup("#okButton").query();
        clickOn(okButton);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(event).addParticipant(p1);
    }

    @Test
    public void deleteAndUndoParticipantTest() throws CloneNotSupportedException, InterruptedException {
        /// this test creates adds a Participant, then deletes it and finally undoes the deletion,
        /// testing the frontend along the way
        /// except the dynamic Delete button, I just couldn't get that to work ...
        String name = "casian";
        String email = "aa@bb.com";
        String iban = "AD1400080001001234567890";
        String bic = "SMCOGB2L";
        Event event = new Event("asdf", "123", LocalDateTime.now());//Mockito.mock(Event.class);
        Participant p1 = new Participant(name, email, event, iban, bic);
        controller.setEvent(event);
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        controller.refresh();
        Platform.runLater(() -> {
            try {
                controller.add();
                controller.setPotentialRemover(controller.getEvent().getParticipants().get(0));
                controller.remove();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Button ok = lookup("#undoButton").query();
        clickOn(ok); /// presses undo button
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(p1, controller.getEvent().getParticipants().get(0));
    }

    @Test
    public void failedDeleteTest() throws CloneNotSupportedException, InterruptedException {
        String name = "casian";
        String email = "aa@bb.com";
        String iban = "AD1400080001001234567890";
        String bic = "SMCOGB2L";
        Event event = new Event("asdf", "123", LocalDateTime.now());
        controller.setEvent(event);
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        controller.refresh();
        Participant p1 = new Participant(name, email, event, iban, bic);
        Platform.runLater(() -> {
            try {
                controller.add();
                controller.getEvent().getParticipants().get(0).addExpenseInvolved(Mockito.mock(Expense.class));
                controller.setPotentialRemover(controller.getEvent().getParticipants().get(0));
                controller.remove();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> {
            controller.remove(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Failed to remove participant because they do not exist.", controller.getDebugInfo());
    }

    @Test
    public void keyboardTesting() throws CloneNotSupportedException, InterruptedException {
        AddParticipantCtrl spy = Mockito.spy(controller);
        spy.keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE,
                false, false, false, false));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(spy).goBack();
    }

    @Test
    public void keyboardTesting2() throws CloneNotSupportedException, InterruptedException {
        controller.setEvent(Mockito.mock(Event.class));
        AddParticipantCtrl spy = Mockito.spy(controller);
        spy.keyPressed(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.R,
                false, false, false, false));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(spy).refresh();
    }

    @Test
    public void gettersTest() throws CloneNotSupportedException, InterruptedException {
        controller.setEvent(Mockito.mock(Event.class));
        AddParticipantCtrl spy = Mockito.spy(controller);
        spy.getDebugInfo();
        spy.getIbanField();
        spy.getScene();
        spy.getBicField();
        spy.getEmailField();
        spy.getNameField();
        spy.goBack();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(spy).getDebugInfo();
        Mockito.verify(spy).getIbanField();
        Mockito.verify(spy).getScene();
        Mockito.verify(spy).getBicField();
        Mockito.verify(spy).getEmailField();
        Mockito.verify(spy).getNameField();
        Mockito.verify(spy).goBack();
    }

    @Test
    public void updateAndUndoParticipantTest() throws CloneNotSupportedException, InterruptedException {
        String name = "casian";
        String email = "aa@bb.com";
        String iban = "AD1400080001001234567890";
        String bic = "SMCOGB2L";
        Event event = new Event("asdf", "123", LocalDateTime.now());//Mockito.mock(Event.class);
        Participant p1 = new Participant(name, email, event, iban, bic);
        controller.setEvent(event);
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        controller.refresh();
        Platform.runLater(() -> {
            try {
                controller.add();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        bic = "SMCOGB2A"; /// mofified it
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        Platform.runLater(() -> {
            try {
                controller.add();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        Button ok = lookup("#undoButton").query();
        clickOn(ok);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(p1, controller.getEvent().getParticipants().get(0));
        clickOn(ok); /// presses undo button
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("No actions to undo! Event may have been updated.", controller.getDebugInfo());
    }

    @Test
    public void validatorsTest() throws CloneNotSupportedException, InterruptedException {
        String name = "casian";
        String email = "aa@bb.";
        String iban = "AD1400080001001234567890";
        String bic = "SMCOGB2L";
        Event event = new Event("asdf", "123", LocalDateTime.now());//Mockito.mock(Event.class);
        controller.setEvent(event);
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        Platform.runLater(() -> {
            try {
                controller.add();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Invalid email!", controller.getDebugInfo());
        email = "aa@bb.com";
        iban = "AD140008000 AFASFDAFD 1001234567890";
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        controller.cancelDeletion();
        Platform.runLater(() -> {
            try {
                controller.add();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Invalid IBAN!", controller.getDebugInfo());
        iban = "AD1400080001001234567890";
        bic = "SMCOGB2L AFASFAS";
        controller.getNameField().setText(name);
        controller.getEmailField().setText(email);
        controller.getBicField().setText(bic);
        controller.getIbanField().setText(iban);
        Platform.runLater(() -> {
            try {
                controller.add();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Invalid BIC!", controller.getDebugInfo());
    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }
}