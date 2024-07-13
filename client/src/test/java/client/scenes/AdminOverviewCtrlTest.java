package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import jakarta.ws.rs.BadRequestException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class AdminOverviewCtrlTest extends ApplicationTest {
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @Mock
    private TableView<Event> tableView;

    @Mock
    private ObjectMapper om;

    @Mock
    private FileChooser fileChooser;

    @Mock
    private FileWriter fileWriter;

    @InjectMocks
    private AdminOverviewCtrl controller;

    private TableView<Event> fxmlTableView;

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
        Mockito.reset(server, mainCtrl, om, fileChooser, tableView, fileWriter);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<AdminOverviewCtrl, Parent> adminOverview = FXML.load(AdminOverviewCtrl.class,
                "client", "scenes", "AdminOverview.fxml");
        this.controller = adminOverview.getKey();
        fxmlTableView = controller.getTable();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(adminOverview.getValue());
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void testImportJSONNoException() throws Exception {
        File file = Mockito.mock(File.class);
        Event event = Mockito.mock(Event.class);
        Mockito.when(fileChooser.showOpenDialog(null)).thenReturn(file);
        Mockito.when(om.readValue(file, Event.class)).thenReturn(event);

        Button importButton = lookup("#importButton").queryButton();
        clickOn(importButton);

        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).send("/app/events/import", event);
    }


    @Test
    public void testImportJSONShowsAlertOnIOException() throws Exception {
        File file = Mockito.mock(File.class);
        Mockito.when(fileChooser.showOpenDialog(null)).thenReturn(file);
        Mockito.doThrow(new IOException()).when(om).readValue(file, Event.class);

        Button importButton = lookup("#importButton").queryButton();
        clickOn(importButton);

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("OK", NodeMatchers.isVisible());
    }

    @Test
    public void testDownloadJSONNoException() throws Exception {
        File tempFile = Mockito.mock(File.class);
        Event event = Mockito.mock(Event.class);
        TableView.TableViewSelectionModel<Event> selectionModel =
                Mockito.mock(TableView.TableViewSelectionModel.class);
        ObjectWriter objectWriter = Mockito.mock(ObjectWriter.class);

        Mockito.when(tableView.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(tempFile.getPath()).thenReturn("some/path");
        Mockito.when(selectionModel.getSelectedItem()).thenReturn(event);
        Mockito.when(om.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(fileChooser.showSaveDialog(null)).thenReturn(tempFile);
        Mockito.doNothing().when(fileWriter).write(Mockito.anyString());
        Mockito.when(objectWriter.writeValueAsString(event)).thenReturn("json");

        Button downloadButton = lookup("#downloadButton").queryButton();
        clickOn(downloadButton);

        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(tableView).getSelectionModel();
        Mockito.verify(selectionModel).getSelectedItem();
        Mockito.verify(fileChooser).showSaveDialog(null);
        Mockito.verify(fileWriter).write("json");
        Mockito.verify(fileWriter).close();
    }

    @Test
    public void testDownloadJSONShowsAlertOnIOException() throws Exception {
        File tempFile = Mockito.mock(File.class);
        Event event = Mockito.mock(Event.class);
        TableView.TableViewSelectionModel<Event> selectionModel =
                Mockito.mock(TableView.TableViewSelectionModel.class);
        ObjectWriter objectWriter = Mockito.mock(ObjectWriter.class);

        Mockito.when(tableView.getSelectionModel()).thenReturn(selectionModel);
        Mockito.when(tempFile.getPath()).thenReturn("some/path");
        Mockito.when(selectionModel.getSelectedItem()).thenReturn(event);
        Mockito.when(om.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        Mockito.when(fileChooser.showSaveDialog(null)).thenReturn(tempFile);
        Mockito.doThrow(new IOException()).when(fileWriter).write(Mockito.anyString());
        Mockito.when(objectWriter.writeValueAsString(event)).thenReturn("json");

        Button downloadButton = lookup("#downloadButton").queryButton();
        clickOn(downloadButton);

        WaitForAsyncUtils.waitForFxEvents();

        Mockito.verify(tableView).getSelectionModel();
        Mockito.verify(selectionModel).getSelectedItem();
        Mockito.verify(fileChooser).showSaveDialog(null);
        Mockito.verify(fileWriter).write("json");
        verifyThat("OK", NodeMatchers.isVisible());
    }


    @Test
    public void testStop() {
        controller.stop();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).stop();
    }

    @Test
    public void testToStartScreen() {
        controller.toStartScreen();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showStartScreen();
    }

    @Test
    public void testRefresh() {
        controller.setTable(fxmlTableView);
        List<Event> eventList = new ArrayList<>();
        Event event = Mockito.mock(Event.class);
        eventList.add(event);
        fxmlTableView.getItems().add(event);
        Mockito.when(server.getEvents()).thenReturn(eventList);
        controller.refresh();
        Mockito.verify(server).getEvents();
        assertEquals(eventList, new ArrayList<>(controller.getTable().getItems()));
        controller.setTable(tableView);
    }

    @Test
    public void testDelete() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        fxmlTableView.getItems().add(event);
        fxmlTableView.getSelectionModel().select(event);
        Platform.runLater(() -> controller.delete());
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).send("/app/events/delete", event);
        Mockito.verify(mainCtrl).removeFromRecentEvents(event);
        controller.setTable(tableView);
    }

    @Test
    public void testCellFactory() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        assertEquals(0, fxmlTableView.getItems().size());
        fxmlTableView.getItems().add(event);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, fxmlTableView.getItems().size());
        controller.setTable(tableView);
    }

    @Test
    public void testConnectLongPollingAdd() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        Mockito.when(server.getEvent(event.getInviteCode())).thenReturn(event);
        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectLongPolling();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).registerForUpdates(captor.capture());
        captor.getValue().accept(event);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).getEvent(event.getInviteCode());
        assertTrue(controller.getEvents().contains(event));
        controller.setTable(tableView);
    }


    @Test
    public void testConnectLongPollingUpdate() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        Mockito.when(server.getEvent(event.getInviteCode())).thenReturn(event);
        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectLongPolling();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).registerForUpdates(captor.capture());
        captor.getValue().accept(event);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).getEvent(event.getInviteCode());
        assertTrue(controller.getEvents().contains(event));
        controller.setTable(tableView);
    }

    @Test
    public void testConnectLongPollingUpdateEvent() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        fxmlTableView.getItems().add(event);

        Event updatedEvent = Mockito.mock(Event.class);
        Mockito.when(updatedEvent.getInviteCode()).thenReturn("inviteCode");
        Mockito.when(server.getEvent(updatedEvent.getInviteCode())).thenReturn(updatedEvent);

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectLongPolling();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).registerForUpdates(captor.capture());
        captor.getValue().accept(updatedEvent);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).getEvent(updatedEvent.getInviteCode());
        assertTrue(controller.getEvents().contains(updatedEvent));
        controller.setTable(tableView);
    }

    @Test
    public void testConnectLongPollingDeleteEvent() {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn("inviteCode");
        fxmlTableView.getItems().add(event);

        Mockito.when(server.getEvent(event.getInviteCode())).thenThrow(BadRequestException.class);

        ArgumentCaptor<Consumer<Event>> captor = ArgumentCaptor.forClass(Consumer.class);
        controller.connectLongPolling();
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).registerForUpdates(captor.capture());
        captor.getValue().accept(event);
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(server).getEvent(event.getInviteCode());
        assertFalse(controller.getEvents().contains(event));
        controller.setTable(tableView);
    }

    @Test
    public void testMousePressedEvent() throws InterruptedException {
        controller.setTable(fxmlTableView);
        Event event = Mockito.mock(Event.class);
        fxmlTableView.getItems().add(event);
        WaitForAsyncUtils.waitForFxEvents();
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(fxmlTableView.getSelectionModel().isSelected(0));
        clickOn(row);
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(fxmlTableView.getSelectionModel().isSelected(0));
        controller.setTable(tableView);
    }
}