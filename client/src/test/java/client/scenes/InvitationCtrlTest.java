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
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
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
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class InvitationCtrlTest extends ApplicationTest {

    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;
    @Mock
    private Event event;
    @InjectMocks
    private InvitationCtrl controller;

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
        Mockito.reset(mainCtrl, server, event);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<InvitationCtrl, Parent> editTitle = FXML.load(InvitationCtrl.class,
                "client", "scenes", "Invitation.fxml");
        this.controller = editTitle.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(editTitle.getValue());
        this.sceneVerification = scene;
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(e2 -> controller.keyPressed(e2));
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void goBackTest() {
        type(KeyCode.ESCAPE);
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void getSceneTest() {
        assertEquals(sceneVerification, controller.getScene());
    }

    @Test
    public void emailValidNullTest() {
        String email = null;
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidEmptyTest() {
        String email = "";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidNoAtSymbolTest() {
        String email = "email";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidNoDomainTest() {
        String email = "email@";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidNoNameTest() {
        String email = "@email";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidInvalidDomainTest() {
        String email = "e@email";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidInvalidDomainNoHostTest() {
        String email = "e@.";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidNoTopLevelDomainTest() {
        String email = "e@h.";
        assertFalse(controller.emailValid(email));
    }

    @Test
    public void emailValidSuccessfulTest() {
        String email = "myemail@gmail.com";
        assertTrue(controller.emailValid(email));
    }

    @Test
    public void sendInviteSuccessfulTest() {
        String sub = "You have been invited to a Splitty Event!";

        clickOn("#emails");
        write("1@email.com");
        type(KeyCode.ENTER);
        write("2@email.com");
        clickOn("#sendInvite");
        Mockito.verify(server).sendMail(eq("1@email.com"), eq(sub), Mockito.anyString());
        Mockito.verify(server).sendMail(eq("2@email.com"), eq(sub), Mockito.anyString());
        verifyThat("Action completed!", NodeMatchers.isVisible());
    }

    @Test
    public void sendInviteNoEmailsTest() {
        clickOn("#sendInvite");
        verifyThat("The text field is empty", NodeMatchers.isVisible());
    }

    @Test
    public void sendInviteInvalidEmailsTest() {
        clickOn("#emails");
        write("1");
        clickOn("#sendInvite");
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void sendInviteExceptionTest() {
        String sub = "You have been invited to a Splitty Event!";
        Mockito.doThrow(new WebApplicationException()).when(server)
                .sendMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        clickOn("#emails");
        write("1@email.com");
        clickOn("#sendInvite");
        Mockito.verify(server).sendMail(eq("1@email.com"), eq(sub), Mockito.anyString());
        verifyThat("Error: 1@email.com", NodeMatchers.isVisible());
    }

    @Test
    public void clipCopyTest() {
        AtomicReference<Clipboard> clipboard = new AtomicReference<>();
        Platform.runLater(() -> clipboard.set(Clipboard.getSystemClipboard()));
        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> assertNotEquals("123456",
                clipboard.get().getString()));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.when(event.getInviteCode()).thenReturn("123456");
        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#codeLabel");
        Platform.runLater(() -> assertEquals("123456",
                clipboard.get().getString()));
    }
}