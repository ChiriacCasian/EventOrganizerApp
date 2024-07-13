package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class EditTitleCtrlTest extends ApplicationTest {

    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;
    @Mock
    private Event event;
    @InjectMocks
    private EditTitleCtrl controller;

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
        Pair<EditTitleCtrl, Parent> editTitle = FXML.load(EditTitleCtrl.class,
                "client", "scenes", "EditTitle.fxml");
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
    public void getSceneTest() {
        assertEquals(sceneVerification, controller.getScene());
    }

    @Test
    public void refresh() {
        assertDoesNotThrow(() -> controller.refresh());
    }

    @Test
    public void goBackTest() {
        press(KeyCode.ESCAPE);
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void editTitleTest() {
        String oldTitle = "old title";
        String newTitle = "new title";

        Mockito.when(event.getTitle()).thenReturn(oldTitle);

        clickOn("#newTitle");
        write(newTitle);
        press(KeyCode.ENTER);
        Mockito.verify(event).setTitle(newTitle);
        Mockito.verify(server).send("/app/events/update", event);
        Mockito.verify(event).setDateOfModification(Mockito.any(LocalDateTime.class));
        Mockito.verify(mainCtrl).showEventOverview(event);
    }

    @Test
    public void editTitleExceptionTest() {
        String oldTitle = "old title";
        String newTitle = "new title";

        Mockito.when(event.getTitle()).thenReturn(oldTitle);
        Mockito.doThrow(new WebApplicationException())
                .when(server).send("/app/events/update", event);

        clickOn("#newTitle");
        write(newTitle);
        press(KeyCode.ENTER);
        Mockito.verify(event).setTitle(newTitle);
        Mockito.verify(server).send("/app/events/update", event);
        Mockito.verify(event).setTitle(oldTitle);
        verifyThat("Input not valid!", NodeMatchers.isVisible());
    }
}