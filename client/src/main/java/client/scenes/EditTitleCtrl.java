package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.time.LocalDateTime;

public class EditTitleCtrl extends WebSocketConnector {


    @FXML
    private TextField newTitle;

    @FXML
    private Button editTitle;

    @FXML
    private Button cancel;

    @FXML
    private Label title;


    /**
     * Constructs an instance of EditTitleCtrl
     *
     * @param server   - EventUtils used for interaction with the server
     * @param mainCtrl - used for switching scenes
     */
    @Inject
    public EditTitleCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the languages
     */
    @FXML
    public void initialize() {
        title.textProperty().bind(Language.createStringBinding("editTitleTitle"));
        cancel.textProperty().bind(Language.createStringBinding("editTitleCancel"));
        editTitle.textProperty().bind(Language.createStringBinding("editTitleEdit"));
    }

    /**
     * goes back to EventOverview
     */
    public void goBack() {
        newTitle.clear();
        mainCtrl.showEventOverview(event);
    }

    /**
     * updates the event in the database
     */
    public void editTitle() {
        String title = event.getTitle();
        if (newTitle.getText() != null && !newTitle.getText().isEmpty()) {
            try {
                event.setTitle(newTitle.getText());
                event.setDateOfModification(LocalDateTime.now());
                server.send("/app/events/update", event);
                mainCtrl.showEventOverview(event);
            } catch (WebApplicationException ex) {
                event.setTitle(title);
                newTitle.clear();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.headerTextProperty().bind(Language.createStringBinding(
                        "editTitleInvalidHeader"));
                errorAlert.contentTextProperty().bind(Language.createStringBinding(
                        "editTitleInvalidContent"));
                errorAlert.showAndWait();
            }
        }
    }

    /**
     * Handles keys pressed, when enter pressed, goes to editTitle, when escape , goes to cancel
     *
     * @param e2 the keyEvent selected
     */
    public void keyPressed(KeyEvent e2) {
        switch (e2.getCode()) {
            case ENTER -> editTitle();
            case ESCAPE -> goBack();
        }
    }

    /**
     * Refresh method required by superclass
     */
    public void refresh() {
    }

    /**
     * Returns the scene of the controller
     *
     * @return the scene of the controller
     */
    public Scene getScene() {
        return newTitle.getScene();
    }
}
