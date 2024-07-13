package client.scenes;

import client.utils.Language;
import client.utils.LanguageSelector;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.time.LocalDateTime;
import java.util.*;


public class StartScreenCtrl implements LanguageSelector {

    private ServerUtils server;

    private MainCtrl mainCtrl;

    private Map<String, Event> events;

    @FXML
    private MenuButton languageIndicator;

    @FXML
    private MenuItem english;

    @FXML
    private MenuItem dutch;

    @FXML
    private MenuItem bulgarian;

    @FXML
    private MenuItem german;

    @FXML
    private MenuItem contribute;

    @FXML
    private TextField newEvent;

    @FXML
    private ListView<String> rEvents;

    @FXML
    private TextField toJoinEvent;

    @FXML
    private Button createEvent;

    @FXML
    private Button joinEvent;

    @FXML
    private Button toLogin;

    @FXML
    private Label recentlyViewed;

    @FXML
    private Label joinEventLabel;

    @FXML
    private TextField newDesc;

    @FXML
    private Label createEventLabel;

    @FXML
    private Label enterName;

    @FXML
    private Label enterDescription;

    /**
     * Constructs an instance of StartScreenCtrl with the specified dependencies
     *
     * @param server   ServerUtils instance used for server used to communicate with the server
     * @param mainCtrl the mainCtrl for main application flow
     */
    @Inject
    public StartScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the required fields.
     */
    @FXML
    public void initialize() {
        this.events = new LinkedHashMap<>();
        joinEvent.textProperty().bind(Language.createStringBinding("startScreenJoin"));
        createEvent.textProperty().bind(Language.createStringBinding("startScreenCreate"));
        recentlyViewed.textProperty().bind(Language.createStringBinding
                ("startScreenRecentlyViewed"));
        joinEventLabel.textProperty().bind(Language.createStringBinding("startScreenJoinEvent"));
        createEventLabel.textProperty().bind(Language.createStringBinding
                ("startScreenCreateNewEvent"));
        toLogin.textProperty().bind(Language.createStringBinding("startScreenAdminLogin"));
        enterName.textProperty().bind(Language.createStringBinding("startScreenEnterName"));
        enterDescription.textProperty().bind(Language.createStringBinding
                ("startScreenEnterDescription"));

        languageIndicator.setMinSize(flagWidth, flagHeight);
        languageIndicator.setMaxSize(flagWidth, flagHeight);
        languageIndicator.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("styles.css"))
                        .toExternalForm());
        languageIndicator.setStyle("-fx-background-color: transparent;");
    }

    /**
     * @return the ListView of recent events
     */
    public ListView<String> getREvents() {
        return rEvents;
    }

    /**
     * Initializes the StartScreenCtrl with a WebSocket connection
     */
    @FXML
    public void joinEvent() {
        String code = toJoinEvent.getText();
        if (code.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.headerTextProperty().bind(Language
                    .createStringBinding("editTitleInvalidHeader"));
            errorAlert.contentTextProperty().bind(Language
                    .createStringBinding("enterValidInviteCode"));
            errorAlert.showAndWait();
        } else {
            try {
                Event e = server.getEvent(code);
                e.updateFields();
                mainCtrl.showEventOverview(e);
            } catch (WebApplicationException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.headerTextProperty().bind(Language
                        .createStringBinding("editTitleInvalidHeader"));
                errorAlert.contentTextProperty().bind(Language
                        .createStringBinding("enterValidInviteCode"));
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * @return the TextField for the new event
     */
    public TextField getToJoinEvent() {
        return toJoinEvent;
    }

    /**
     * Connects to the server via WebSocket.
     */
    public void connectWebSocket() {
        server.registerForUpdatesWS("/topic/events/update", Event.class, e ->
                Platform.runLater(this::refresh));
        server.registerForUpdatesWS("/topic/events/delete", Event.class, e -> {
            mainCtrl.removeFromRecentEvents(e);
            events.remove(e.getInviteCode());
            Platform.runLater(this::refresh);
        });
        server.registerForUpdatesWS("/topic/events/import", Event.class, e ->
                Platform.runLater(this::refresh));
    }

    /**
     * Creates an Event by providing the name, or by going to a different scene to fill more info
     */
    @FXML
    public void createEvent() {
        Event g = this.getEvent();
        if (g.getTitle().length() >= 18) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.headerTextProperty().bind(Language.createStringBinding("eventTitleTooLong"));
            errorAlert.contentTextProperty().bind(Language
                    .createStringBinding("eventTitleCharacterLimit"));
            errorAlert.showAndWait();
        } else {
            try {
                server.addEvent(g);
                g.updateFields();
                mainCtrl.showEventOverview(g);
            } catch (WebApplicationException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.headerTextProperty()
                        .bind(Language.createStringBinding("editTitleInvalidHeader"));
                errorAlert.contentTextProperty().bind(Language
                        .createStringBinding("eventTitleEnterValid"));
                errorAlert.showAndWait();
            }
            clearFields();
        }
    }

    /**
     * @return returns the event which we create
     */
    public Event getEvent() {
        String desc = newDesc.getText();
        return new Event(newEvent.getText(), desc, "EUR", LocalDateTime.now());
    }

    /**
     * Shows recently visited events
     */
    public void showRecent() {
        for (Event e : mainCtrl.recentEventsSublist()) {
            try {
                events.put(e.getInviteCode(), server.getEvent(e.getInviteCode()));
            } catch (WebApplicationException ex) {
                mainCtrl.removeFromRecentEvents(e);
            }
        }
        List<String> rTitles = events.values()
                .stream()
                .map(e -> e.getTitle() + " - " + e.getInviteCode())
                .toList();
        int size = Math.max(0, rTitles.size() - 4);
        rEvents.getItems().setAll(FXCollections.observableList(
                rTitles.subList(size, rTitles.size())));
        rEvents.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(ObservableValue<? extends String>
                                                observable, String oldValue, String newValue) {
                        String e1 = rEvents.getSelectionModel().getSelectedItem();
                        if (e1 == null) return;
                        e1 = e1.substring(e1.indexOf("-") + 2);
                        events.get(e1).updateFields();
                        mainCtrl.showEventOverview(events.get(e1));
                    }
                });
    }

    /**
     * Refreshes the scene
     */
    public void refresh() {
        showRecent();
    }

    /**
     * Clears the fields
     */
    public void clearFields() {
        newDesc.clear();
        toJoinEvent.clear();
        newEvent.clear();
    }

    /**
     * Directs to log in page
     */
    public void toLogin() {
        mainCtrl.showLogin();
    }

    /**
     * Joins an event when Enter is hit whenever in the join event text field
     *
     * @param keyEvent the key that was pressed
     */
    public void joinEventKeyAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) joinEvent();
    }

    /**
     * Creates an event when Enter is hit whenever in the create event text field
     *
     * @param keyEvent the key that was pressed
     */
    public void createEventKeyAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) createEvent();
    }

    /**
     * Sets the language
     *
     * @param locale the locale
     */
    public void setLanguage(Locale locale) {
        Language.setLanguage(locale);
        setLanguageButtonGraphic();
    }

    /**
     * Returns the main controller
     *
     * @return the main controller
     */
    @Override
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    /**
     * Returns the language indicator
     *
     * @return the language indicator
     */
    @Override
    public MenuButton getLanguageIndicator() {
        return languageIndicator;
    }

    /**
     * Returns the menu item for the English language
     *
     * @return the menu item for the English language
     */
    @Override
    public MenuItem getEnglish() {
        return english;
    }

    /**
     * Returns the menu item for the Dutch language
     *
     * @return the menu item for the Dutch language
     */
    @Override
    public MenuItem getDutch() {
        return dutch;
    }

    /**
     * Returns the menu item for the Bulgarian language
     *
     * @return the menu item for the Bulgarian language
     */
    @Override
    public MenuItem getBulgarian() {
        return bulgarian;
    }

    /**
     * Returns the menu item for the German language
     *
     * @return the menu item for the German language
     */
    @Override
    public MenuItem getGerman() {
        return german;
    }

    /**
     * Returns the menu item for contributing a new language
     *
     * @return the menu item for contributing a new language
     */
    @Override
    public MenuItem getContribute() {
        return contribute;
    }
}