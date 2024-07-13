package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Event;
import commons.Expense;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class EditExpenseTypeCtrl extends WebSocketConnector {
    private Event event;
    private Expense expense;
    private Map<String, String> allTypes;
    private Map.Entry<String, String> type;

    @FXML
    private Label expenseType;
    @FXML
    private TextField newExpenseType;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Label title;
    @FXML
    private Label expenseTypeLabel;
    @FXML
    private Label color;
    @FXML
    private Button save;
    @FXML
    private Button back;

    /**
     * Constructor for the controller
     *
     * @param server   the ServerUtils instance
     *                 used for server communication for Expense management
     * @param mainCtrl the MainCtrl instance used for controlling the main application flow
     */
    @Inject
    public EditExpenseTypeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the language properties
     */
    @FXML
    public void initialize() {
        allTypes = new HashMap<>();
        title.textProperty().bind(Language.createStringBinding("editExpenseTypeEditTag"));
        expenseTypeLabel.textProperty().bind(Language.createStringBinding
                ("editExpenseTypeExpenseType"));
        color.textProperty().bind(Language.createStringBinding("editExpenseTypeColor"));
        save.textProperty().bind(Language.createStringBinding("manageExpenseSave"));
        back.textProperty().bind(Language.createStringBinding("eventOverviewBack"));
    }


    /**
     * refresh method without arguments
     */
    public void refresh() {
    }


    /**
     * Returns the scene of the controller
     *
     * @return the scene of the controller
     */
    public Scene getScene() {
        return title.getScene();
    }

    /**
     * Clears the fields on the scene when it is closed
     */
    public void clearFields() {
        this.expenseType.setText("");
        this.newExpenseType.clear();
        this.colorPicker.setStyle("-fx-color-label-visible: false ;");
    }

    /**
     * Saves the changes made to the expense type
     */
    public void ok() {
        setNewExpenseType();
    }

    /**
     * Closes this scene and returns to the previous one
     */
    public void cancel() {
        mainCtrl.showManageExpense(event, expense);
        clearFields();
    }

    /**
     * Deal with the key shortcuts
     *
     * @param e the key event which gives information of pressed key
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER -> ok();
            case ESCAPE -> cancel();
        }
    }

    /**
     * Sets the expense of this controller instance
     *
     * @param expense the expense to put as a value
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * Sets the expense type of this controller instance
     *
     * @param type the expense type to put as a value
     */
    public void setExpenseType(Map.Entry<String, String> type) {
        this.type = type;
        this.expenseType.setText(type.getKey());
    }

    /**
     * Sets the event of this controller instance
     *
     * @param event the event
     */
    public void setEvent(Event event) {
        this.event = event;
        refresh();
    }

    /**
     * Returns the hex code of a given color
     *
     * @param color the color whose code we want
     * @return the hex code as a String
     */
    public static String colorToHexCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Setting new expense type value
     */
    public void setNewExpenseType() {
        String newType = newExpenseType.getText();
        if (newType != null && !newType.isEmpty() && !allTypes.containsKey(newType)) {
            Color newColor = colorPicker.getValue();
            String hex = colorToHexCode(newColor);
            this.expense.getExpenseTypes().remove(type.getKey());
            for (Expense e : event.getExpenses()) {
                if (e.getExpenseTypes().containsKey(type.getKey())) {
                    e.getExpenseTypes().remove(type.getKey());
                    e.getExpenseTypes().put(newType, hex);
                }
            }
            this.expense.getExpenseTypes().put(newType, hex);
            this.type = new AbstractMap.SimpleEntry<>(newType, hex);
            mainCtrl.showManageExpense(event, expense);
            clearFields();
        } else {
            showAlert(Alert.AlertType.WARNING, Modality.APPLICATION_MODAL,
                    "careful", "cannotPerformThisOperation",
                    "nullExpenseError");
        }
    }

    /**
     * Shows an alert with the given parameters
     *
     * @param type        the type of the alert
     * @param modality    the modality of the alert
     * @param title       the title of the alert
     * @param headerText  the header text of the alert
     * @param contentText the content text of the alert
     */
    public void showAlert(Alert.AlertType type, Modality modality,
                          String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.initModality(modality);
        alert.titleProperty().bind(Language.createStringBinding(title));
        alert.headerTextProperty().bind(Language.createStringBinding(headerText));
        alert.contentTextProperty().bind(Language.createStringBinding(contentText));
        alert.showAndWait();
    }
}
