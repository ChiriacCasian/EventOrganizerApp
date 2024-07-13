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
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.testfx.api.FxAssert.verifyThat;

class DebtOverviewCtrlTest extends ApplicationTest {

    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @Mock
    private Event event;

    @InjectMocks
    private DebtOverviewCtrl controller;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "0");
        System.setProperty("from.email", "splittyoopp@gmail.com");
        System.setProperty("from.password", "dqthehoktabtjdmq");
        System.setProperty("configPath", "src/main/resources/config.properties");
    }

    @AfterEach
    public void resetMocks() {
        Mockito.reset(server, mainCtrl);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<DebtOverviewCtrl, Parent> debtOverview = FXML.load(DebtOverviewCtrl.class,
                "client", "scenes", "DebtOverview.fxml");
        this.controller = debtOverview.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(debtOverview.getValue());
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void refreshTest() {
        String basicInfo = "basic info";
        String paymentInstructions = "payment instructions";
        Debt debt = Mockito.mock(Debt.class);
        Participant participant = Mockito.mock(Participant.class);
        Mockito.when(debt.getPayer()).thenReturn(participant);
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(event.debtOverview()).thenReturn(new HashSet<>(Set.of(debt)));
        Mockito.when(debt.basicInfo(Language.getLanguage().getLanguage())).thenReturn(basicInfo);
        Mockito.when(debt.paymentInstructions(Language.getLanguage().getLanguage())).thenReturn(paymentInstructions);
        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(event, Mockito.atLeastOnce()).debtOverview();
        assertEquals(basicInfo, controller.getBasicInfo().get(debt));
        assertEquals(paymentInstructions, controller.getPaymentInstructions().get(debt));
        assertFalse(controller.getShowExpand().get(debt));
    }

    @Test
    public void goBackTest() {
        controller.goBack();
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void getSceneTest() {
        assertEquals(controller.getScene(), controller.getScene());
    }

    @Test
    public void sendReminderTest() {
        Participant participant = Mockito.mock(Participant.class);
        Debt debt = new Debt(participant, participant,
                20.0, "euro");
        Mockito.when(event.debtOverview()).thenReturn(new HashSet<>(Set.of(debt)));
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(server.sendMail(eq(participant.getEmail()), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("invite code");
        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#expandCollapseButton");
        clickOn("#reminderButton");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).sendMail(eq(participant.getEmail()), Mockito.anyString(), Mockito.anyString());
        verifyThat("Action completed!", NodeMatchers.isVisible());
    }

    @Test
    public void sendReminderExceptionTest() {
        Participant participant = Mockito.mock(Participant.class);
        Debt debt = new Debt(participant, participant,
                20.0, "euro");
        Mockito.when(event.debtOverview()).thenReturn(new HashSet<>(Set.of(debt)));
        Mockito.when(participant.getName()).thenReturn("name");
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(server.sendMail(eq(participant.getEmail()), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new WebApplicationException("error"));

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#expandCollapseButton");
        clickOn("#reminderButton");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).sendMail(eq(participant.getEmail()), Mockito.anyString(), Mockito.anyString());
        verifyThat("Email does not exist",
                NodeMatchers.isVisible());
    }

    @Test
    public void settleButton() {
        Participant participant = Mockito.mock(Participant.class);
        Mockito.when(participant.getEmail()).thenReturn("email@gmail.com");
        Debt debt = new Debt(participant, participant,
                20.0, "euro");
        Mockito.when(event.debtOverview()).thenReturn(new HashSet<>(Set.of(debt)));

        Platform.runLater(() -> controller.refresh());
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#expandCollapseButton");
        clickOn("#settleButton");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showSettleDebt(event, debt);
    }

}