package client.scenes;


import client.utils.Language;
import client.utils.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AdminOverviewCtrl {

    private ServerUtils server;

    private ObservableList<Event> events;

    private ObjectMapper om;
    private FileChooser fileChooser;
    private FileWriter fileWriter;

    private MainCtrl mainCtrl;

    @FXML
    private TableView<Event> table;
    @FXML
    private TableColumn<Event, String> colTitle;
    @FXML
    private TableColumn<Event, String> colDescription;
    @FXML
    private TableColumn<Event, String> colInviteCode;
    @FXML
    private TableColumn<Event, LocalDateTime> colCreated;
    @FXML
    private TableColumn<Event, LocalDateTime> colModified;
    @FXML
    private Label title;
    @FXML
    private Button buttonStart;
    @FXML
    private Button downloadButton;
    @FXML
    private Button importButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button deleteButton;

    /**
     * Constructs an instance of AdminOverviewCtrl with the specified dependencies.
     *
     * @param server      the ServerUtils instance used for server communication
     * @param mainCtrl    the main controller instance used to switch scenes
     * @param om          the ObjectMapper instance used for JSON serialization
     * @param fileChooser the FileChooser instance used for file operations
     * @param table       the TableView instance used to display events
     */
    @Inject
    public AdminOverviewCtrl(ServerUtils server, MainCtrl mainCtrl,
                             ObjectMapper om, FileChooser fileChooser,
                             TableView<Event> table) {
        this.server = server;
        this.events = FXCollections.observableArrayList();
        this.mainCtrl = mainCtrl;
        this.table = table;
        this.om = om;
        this.om.registerModule(new JavaTimeModule());
        this.fileChooser = fileChooser;
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        this.fileChooser.getExtensionFilters().add(extFilter);
    }

    /**
     * Initializes the table.
     */
    @FXML
    public void initialize() {
        table.setItems(events);
        table.getSelectionModel().cellSelectionEnabledProperty().set(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.addEventFilter(MouseEvent.MOUSE_CLICKED, javafx.event.Event::consume);

        colTitle.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getTitle()));
        colDescription.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getDescription()));
        colInviteCode.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getInviteCode()));
        colCreated.setCellValueFactory(q -> {
            LocalDateTime createdDate = q.getValue().getDateOfCreation();
            return new SimpleObjectProperty<>(createdDate);
        });
        colModified.setCellValueFactory(q -> {
            LocalDateTime modifiedDate = q.getValue().getDateOfModification();
            return new SimpleObjectProperty<>(modifiedDate);
        });

        addCopyContextMenu(colTitle);
        addCopyContextMenu(colDescription);
        addCopyContextMenu(colInviteCode);
        addCopyContextMenu(colCreated);
        addCopyContextMenu(colModified);

        table.itemsProperty().bind(new SimpleListProperty<>(events));

        title.textProperty().bind(Language.createStringBinding("adminTitle"));
        buttonStart.textProperty().bind(Language.createStringBinding("adminLogOut"));
        downloadButton.textProperty().bind(Language.createStringBinding("adminDownload"));
        deleteButton.textProperty().bind(Language.createStringBinding("adminDelete"));
        refreshButton.textProperty().bind(Language.createStringBinding("adminRefresh"));
        importButton.textProperty().bind(Language.createStringBinding("adminImport"));
        colCreated.textProperty().bind(Language.createStringBinding("adminColCreated"));
        colDescription.textProperty().bind(Language.createStringBinding("adminColDesc"));
        colInviteCode.textProperty().bind(Language.createStringBinding("adminColCode"));
        colTitle.textProperty().bind(Language.createStringBinding("adminColTitle"));
        colModified.textProperty().bind(Language.createStringBinding("adminColModified"));
    }

    /**
     * Connects to the server via long polling.
     */
    public void connectLongPolling() {
        server.registerForUpdates(e -> {
            try {
                Event tryEvent = server.getEvent(e.getInviteCode());
                if (events.stream().noneMatch(event -> event.getInviteCode()
                        .equals(e.getInviteCode()))) {
                    events.add(e);
                } else {
                    int index = events.indexOf(events.stream()
                            .filter(event -> event.getInviteCode()
                                    .equals(e.getInviteCode()))
                            .findFirst().get());
                    events.set(index, e);
                }
            } catch (Exception ex) {
                events.removeIf(event -> event.getInviteCode().equals(e.getInviteCode()));
            }
        });
    }

    /**
     * Refreshes the data displayed in the TableView by retrieving updated quotes from the server.
     * Updates the TableView items with the retrieved quotes.
     */
    public void refresh() {
        var eventList = server.getEvents();
        events.clear();
        events.addAll(eventList);
    }

    /**
     * Deletes the event displayed in the TableView.
     */
    public void delete() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Delete Event");
            alert.setContentText("Are you sure you want to delete this event?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                server.send("/app/events/delete", selectedEvent);
                mainCtrl.removeFromRecentEvents(selectedEvent);
            } else {
                alert.close();
            }
        }
    }

    /**
     * Downloads a JSON file based on the selected event.
     */
    public void downloadJSON() throws Exception {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();

        if (selectedEvent != null) {
            selectedEvent.updateFields();
            ObjectWriter jacksonWriter = om.writerWithDefaultPrettyPrinter();
            String json = jacksonWriter.writeValueAsString(selectedEvent);
            fileChooser.setTitle("Save JSON File");
            String initialFileName = selectedEvent.getInviteCode() + ".json";
            File initialDirectory = getInitialDirectory();
            fileChooser.setInitialFileName(initialFileName);
            fileChooser.setInitialDirectory(initialDirectory);
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                if (createFileWriter(file) != null)
                    fileWriter = createFileWriter(file);
                try {
                    fileWriter.write(json);
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "error",
                            "errorWritingFile", e.getMessage());
                } finally {
                    fileWriter.close();
                }
            }
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title,
                                  String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.titleProperty().bind(Language.createStringBinding(title));
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Creates a FileWriter instance for the specified file.
     *
     * @param file the file for which a FileWriter instance should be created
     * @return the created FileWriter instance
     */
    public FileWriter createFileWriter(File file) {
        try {
            return new FileWriter(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the initial directory for the file chooser.
     *
     * @return the initial directory for the file chooser
     */
    public File getInitialDirectory() {
        return new File(System.getProperty("user.home"));
    }

    /**
     * Returns the TableView instance used to display events.
     *
     * @return the TableView instance used to display events
     */
    public TableView<Event> getTable() {
        return table;
    }

    /**
     * Sets the TableView instance used to display events.
     *
     * @param table the TableView instance used to display events
     */
    public void setTable(TableView<Event> table) {
        this.table = table;
    }

    /**
     * Imports an event based on the selected JSON file.
     */
    public void importJSON() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                fileChooser.setTitle("Select JSON File");
                Event importedEvent = om.readValue(file, Event.class);
                importedEvent.updateFields();
                importedEvent.setDateOfModification(LocalDateTime.now());
                server.send("/app/events/import", importedEvent);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "error",
                        "errorImportingFile", e.getMessage());
            }
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        server.stop();
    }

    /**
     * Redirects to Start Screen from admin overview
     */
    public void toStartScreen() {
        mainCtrl.showStartScreen();
    }


    /**
     * Adds a context menu to the specified column that allows the user to copy the cell content.
     *
     * @param column the column to which the context menu should be added
     * @param <T>    the type of the column
     */
    private <T> void addCopyContextMenu(TableColumn<Event, T> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    if (item != null) {
                        setText(item.toString());
                    }
                    setContextMenu(createContextMenu());
                    addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        final int index = getIndex();
                        if (index >= 0 && index < getTableView().getItems().size() &&
                                getTableView().getSelectionModel().isSelected(index)) {
                            getTableView().getSelectionModel().clearSelection();
                            event.consume();
                        }
                    });
                }
            }

            private ContextMenu createContextMenu() {
                ContextMenu contextMenu = new ContextMenu();
                MenuItem copyMenuItem = new MenuItem("Copy");
                copyMenuItem.setOnAction(e -> {
                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(getItem().toString());
                    clipboard.setContent(content);
                    table.requestFocus();
                });
                contextMenu.getItems().add(copyMenuItem);
                return contextMenu;
            }
        });
    }


    /**
     * Returns the list of events.
     *
     * @return the list of events
     */
    public ObservableList<Event> getEvents() {
        return events;
    }
}

