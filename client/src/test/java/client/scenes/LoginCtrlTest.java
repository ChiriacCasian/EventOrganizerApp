package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.LoginUtils;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.AdminUser;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(MockitoExtension.class)
class LoginCtrlTest extends ApplicationTest {

    @Mock
    private MainCtrl mainCtrl;
    @Mock
    private ServerUtils server;
    @Mock
    private LoginUtils login;

    @InjectMocks
    private LoginCtrl controller;

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
        Mockito.reset(mainCtrl, server, login);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<LoginCtrl, Parent> login = FXML.load(LoginCtrl.class,
                "client", "scenes", "LoginPage.fxml");
        this.controller = login.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(login.getValue());
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }


    @Test
    public void toAdminOverviewTest() {
        Mockito.when(server.getAdminUser(Mockito.anyString()))
                .thenReturn(new AdminUser("password"));
        clickOn("#passwordField");
        WaitForAsyncUtils.waitForFxEvents();
        write("password");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#logIn");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showAdminOverview();
    }

    @Test
    public void toAdminOverviewExceptionTest() {
        Mockito.when(server.getAdminUser(Mockito.anyString()))
                .thenThrow(new WebApplicationException());
        clickOn("#passwordField");
        WaitForAsyncUtils.waitForFxEvents();
        write("password");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#logIn");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void passGenTest() {
        Mockito.when(login.randPass()).thenReturn("password");
        Mockito.when(server.addUser(Mockito.any(AdminUser.class)))
                .thenReturn(new AdminUser("password"));
        clickOn("#generatePassword");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).addUser(Mockito.any(AdminUser.class));
    }

    @Test
    public void passGenExceptionTest() {
        Mockito.when(login.randPass()).thenReturn("password");
        Mockito.when(server.addUser(Mockito.any(AdminUser.class)))
                .thenThrow(new WebApplicationException());
        clickOn("#generatePassword");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).addUser(Mockito.any(AdminUser.class));
        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void toStartScreenTest() {
        clickOn("#back");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showStartScreen();
    }

    @Test
    public void clearFieldsTest() {
        clickOn("#passwordField");
        WaitForAsyncUtils.waitForFxEvents();
        write("password");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("password", controller.getPasswordField().getText());
        clickOn("#clear");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(controller.getPasswordField().getText().isEmpty());
    }

}