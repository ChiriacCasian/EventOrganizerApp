package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Transaction;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class AddParticipantCtrl extends WebSocketConnector {
    @SuppressWarnings("checkstyle:MemberName")
    @FXML
    private Button testMail;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ibanField;

    @FXML
    private TextField bicField;

    @FXML
    private Label debugInfo;

    private ObservableList<Participant> data;
    private Participant savedId = null;
    private Participant potentialRemover = null;
    private Stack<Pair<Pair<Participant, Participant>, String>> undoAction;

    @FXML
    private TableView<Participant> table;
    @FXML
    private TableColumn<Participant, String> colName;
    @FXML
    private TableColumn<Participant, String> colEmail;
    @FXML
    private TableColumn<Participant, String> colIban;
    @FXML
    private TableColumn<Participant, String> colBic;
    @FXML
    private TableColumn<Participant, String> colDelete;
    @FXML
    private Button cancel;
    @FXML
    private Button deleteButton;
    @FXML
    private Label areYouSure;
    @FXML
    private Button backButton;
    @FXML
    private Button okButton;
    @FXML
    private Button undoButton;
    @FXML
    private Label title;
    @FXML
    private Label name;
    @FXML
    private Label email;
    @FXML
    private Label iban;
    @FXML
    private Label bic;
    @FXML
    private Button cancelButton;

    /**
     * Constructs an instance of AddParticipantCtrl with the specified dependencies.
     *
     * @param server   the ServerUtils instance used for server communication
     * @param mainCtrl the mainCtrl instance used for
     *                 controlling the main application flow
     */
    @Inject
    public AddParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up cell value factories for the TableView columns.
     */
    @SuppressWarnings("checkstyle:MethodLength")
    @FXML
    public void initialize() {
        langInit();

        colName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getName()));
        colName.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getName()));
        colEmail.setCellValueFactory(q -> {
            String email = q.getValue().getEmail();
            if (Participant.uuidValidator(email)) {
                return new SimpleStringProperty("");
            } else {
                return new SimpleStringProperty(email);
            }
        });
        colIban.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getIban()));
        colBic.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getBic()));
        colDelete.setCellValueFactory(q -> new ReadOnlyObjectWrapper<>("DELETE"));
        colDelete.setCellFactory(param -> new TableCell<Participant, String>() {
            private final Button deleteButton = new Button();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                deleteButton.setText(colDelete.getText().toUpperCase());
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;" +
                        " -fx-font-size: 11px; -fx-font-weight: bold;");
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    deleteButton.setOnAction(event -> {
                        Participant participant = getTableView().getItems().get(getIndex());
                        potentialRemover = participant;
                        AddParticipantCtrl.this.deleteButton.textProperty().bind(Language
                                .createStringBinding("acceptDeleteYes"));
                        cancel.textProperty().bind(Language
                                .createStringBinding("acceptDeleteCancel"));
                        areYouSure.textProperty().bind(Language
                                .createStringBinding("acceptDeleteAreYouSure"));
                        mainCtrl.showDeleteScene();
                    });
                    setGraphic(deleteButton);
                    setText(null);
                }
                deleteButton.setPrefWidth(Double.MAX_VALUE);
            }
        });
        undoAction = new Stack<>();
        initTable();
    }

    /**
     * Initializes the languages
     */
    public void langInit() {
        testMail.textProperty().bind(Language.createStringBinding("sendTestMail"));
        backButton.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantBack"));
        okButton.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantOk"));
        undoButton.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantUndo"));
        title.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantTitle"));
        name.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantName"));
        email.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantEmail"));
        iban.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantIBAN"));
        bic.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantBIC"));
        cancelButton.textProperty().bind(Language.createStringBinding
                ("addOrRemoveParticipantCancel"));
        colName.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantName"));
        colBic.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantBIC"));
        colEmail.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantEmail"));
        colIban.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantIBAN"));
        colDelete.textProperty().bind(Language.createStringBinding("addOrRemoveParticipantDelete"));

    }

    /**
     * Connects to the server via websocket and
     * listens for updates to or deletion of the event
     */
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:CyclomaticComplexity"})
    @Override
    public void connectWebSocket() {
        server.registerForUpdatesWS("/topic/events/update", Event.class,
                e -> {
                    if (e.getInviteCode().equals(event.getInviteCode())) {
                        if (e.getParticipants().size() != event.getParticipants().size() ||
                                e.getExpenses().size() != event.getExpenses().size() ||
                                e.getParticipants().stream().map(Participant::getTransactionsFrom)
                                        .flatMap(List::stream).toList().size() !=
                                        event.getParticipants().stream()
                                                .map(Participant::getTransactionsFrom)
                                                .flatMap(List::stream).toList().size()) {
                            undoAction.clear();
                        }
                        event = e;
                        event.updateFields();
                        Platform.runLater(this::refresh);
                    }
                });
        server.registerForUpdatesWS("/topic/events/delete", Event.class, e ->

        {
            if (event != null && e.getInviteCode().equals(event.getInviteCode())) {
                if (mainCtrl.getPrimaryStage().getScene() == this.getScene()) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Splitty");
                        errorAlert.headerTextProperty().bind(Language.createStringBinding("error"));
                        errorAlert.contentTextProperty()
                                .bind(Language.createStringBinding("eventNoLongerExists"));
                        errorAlert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                }
            }
        });
        server.registerForUpdatesWS("/topic/events/import", Event.class,
                e ->
                {
                    if (e.getInviteCode().equals(event.getInviteCode())) {
                        if (e.getParticipants().size() != event.getParticipants().size() ||
                                e.getExpenses().size() != event.getExpenses().size() ||
                                e.getParticipants().stream().map(Participant::getTransactionsFrom)
                                        .flatMap(List::stream).toList().size() !=
                                        event.getParticipants().stream()
                                                .map(Participant::getTransactionsFrom)
                                                .flatMap(List::stream).toList().size()) {
                            undoAction.clear();
                        }
                        event = e;
                        event.updateFields();
                        Platform.runLater(this::refresh);
                    }
                });
    }

    /**
     * this function just initializes the tableView object by adding
     * a listener on each row, so that when you click that row all the attributes
     * are pasted in the texFields, so that you can update that Participant
     */
    private void initTable() {
        table.setOnMouseClicked(event -> {
            Participant participant = table.getSelectionModel().getSelectedItem();
            if (participant != null) {
                savedId = participant;
                try {
                    undoAction.push(new Pair<>(new Pair<>((Participant) participant.clone()
                            , null), "update"));
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                nameField.setText(participant.getName());

                if (Participant.uuidValidator(participant.getEmail())) {
                    emailField.setText("");
                } else {
                    emailField.setText(participant.getEmail());
                }
                ibanField.setText(participant.getIban());
                bicField.setText(participant.getBic());
                debugInfo.setStyle("-fx-text-fill: #008c1d;");
                debugInfo.textProperty().bind(Language.createStringBinding("updateDebugInfoText"));
            }
        });
    }

    /**
     * Clears the fields.
     */
    public void cancelDeletion() {
        potentialRemover = null;
        mainCtrl.showAddParticipant(event);
    }

    /**
     * Clears the fields.
     */
    public void cancel() {
        clearFields();
    }

    /**
     * Handles the confirmation of adding/updating a participant.
     * Attempts to add the participant, displaying an error alert if an exception occurs.
     * Clears the fields and navigates back to the Participants scene upon successful addition.
     * Technology explanation:
     * If adding a participant:
     * - The participant is added to the local event variable.
     * If updating a participant:
     * - The old participant is removed from the local event variable based on their id
     * and the new participant is added.
     * <p>
     * - The event is sent to the server.
     * - The event and its collections are updated in the database.
     * - The entities in the event's collections are correspondingly added, updated, and deleted.
     * - The server sends the updated event to all clients.
     */
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:CyclomaticComplexity"})
    public void add() throws CloneNotSupportedException {
        Participant participant = getParticipant();
        if (participant.getName() == null || participant.getName().isEmpty()) {
            debugInfo.setStyle("-fx-text-fill: #a68600;");
            debugInfo.textProperty().bind(Language.createStringBinding("noNameDebugInfoText"));
            return;
        }
        List<Participant> participants = event.getParticipants();
        boolean ok = participants.contains(participant);
        if (participant.getEmail() == null || participant.getEmail().isEmpty()) {
            participant.setEmail(UUID.randomUUID().toString());
        }
        int debugCode = participant.validate();
        if (debugCode == 1)
            try {
                if (savedId != null) {
                    int index = participants.indexOf(savedId);
                    if (participants.stream().filter(p -> p.getId() != savedId.getId())
                            .map(Participant::getName)
                            .anyMatch(name -> name.equals(participant.getName())) &&
                            participants.stream().filter(p -> p.getId() != savedId.getId())
                                    .map(Participant::getEmail)
                                    .anyMatch(email -> email.equals(participant.getEmail()))) {
                        refresh();
                        clearFields();
                        savedId = null;
                        throw new WebApplicationException("Participant already exists!");
                    }
                    mapOldToNew(participant, participants, index);
                    if (participant.getEmail() == null || participant.getEmail().isEmpty()) {
                        participant.setEmail(savedId.getEmail());
                    }
                    savedId = null;
                    ok = true;
                } else if (ok) {
                    Participant update = participants.get(participants.indexOf(participant));
                    undoAction.push(new Pair<>(new Pair<>((Participant) update.clone()
                            , null), "update"));
                    update.setIban(participant.getIban());
                    update.setBic(participant.getBic());
                } else {
                    event.addParticipant(participant);
                }
                event.setDateOfModification(LocalDateTime.now());
                server.send("/app/events/update", event);
            } catch (WebApplicationException e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                debugInfo.setStyle("-fx-text-fill: #a68600;");
                debugInfo.textProperty().bind(Language
                        .createStringBinding("failedToAddDebugInfoText"));
                alert.showAndWait();
                return;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        else {
            debugAdd(debugCode);
            return;
        }
        refresh();
        clearFields();
        debugInfo.setStyle("-fx-text-fill: #008c1d;");
        if (!ok) {
            debugInfo.textProperty().unbind();
            switch (Language.getLanguage().getLanguage()) {
                case "en": {
                    debugInfo.setText("Successful addition of : " +
                            participant.getName() + " !");
                    break;
                }
                case "nl": {
                    debugInfo.setText("Succesvolle toevoeging van : " +
                            participant.getName() + " !");
                    break;
                }
                case "bg": {
                    debugInfo.setText("\u0423\u0435\u0448\u043d\u043e\u0020" +
                            "\u0434\u043e\u0431\u0430\u0432\u043d\u0435" +
                            "\u0020\u043d\u0430 : " +
                            participant.getName() + " !");
                    break;
                }
                case "de": {
                    debugInfo.setText("Erfolgreiche Hinzuf\u00fcgung von : " +
                            participant.getName() + " !");
                    break;
                }
            }
        } else {
            debugInfo.textProperty().unbind();
            switch (Language.getLanguage().getLanguage()) {
                case "en": {
                    debugInfo.setText("Successful update of : " + participant.getName() + " !");
                    break;
                }
                case "nl": {
                    debugInfo.setText("Succesvolle update van : " + participant.getName() + " !");
                    break;
                }
                case "bg": {
                    debugInfo.setText("\u0423\u0435\u0448\u043d\u0430\u0020\u0430\u043a\u0442" +
                            "\u0443\u0430\u043b\u0438\u0437\u0430\u0446" +
                            "\u0438\u0020\u043d\u0430 : " + participant.getName() + " !");
                    break;
                }
                case "de": {
                    debugInfo.setText("Erfolgreiche Aktualisierung von : " +
                            participant.getName() + " !");
                    break;
                }
            }
            Pair<Pair<Participant, Participant>, String> auxPair = undoAction.pop();
            undoAction.push(new Pair<>(new Pair<>((Participant) auxPair.getKey().getKey().clone(),
                    (Participant) participant.clone()), "update"));
        }
        nameField.requestFocus();
    }

    private void mapOldToNew(Participant participant, List<Participant> participants, int index) {
        participant.setId(savedId.getId());
        participant.setExpensesPaid(savedId.getExpensesPaid());
        participant.setExpensesInvolved(savedId.getExpensesInvolved());
        participant.setTransactionsFrom(savedId.getTransactionsFrom());
        participant.setTransactionsTo(savedId.getTransactionsTo());
        participants.set(index, participant);
    }

    /**
     * function for setting debugInfo depending on the fault
     *
     * @param debugCode the debug code
     */

    @SuppressWarnings("checkstyle:MethodLength")
    public void debugAdd(int debugCode) {
        debugInfo.setStyle("-fx-text-fill: #a68600;");
        switch (debugCode) {
            case 2: {
                debugInfo.textProperty().bind(Language.createStringBinding("invalidEmail"));
                break;
            }
            case 3: {
                debugInfo.textProperty().bind(Language.createStringBinding("invalidIBAN"));
                break;
            }
            case 4: {
                debugInfo.textProperty().bind(Language.createStringBinding("invalidBIC"));
                break;
            }
        }
    }

    /**
     * the undo function for undoAction
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    public void undo() {
        if (undoAction.isEmpty()) {
            debugInfo.textProperty().bind(Language.createStringBinding("noUndo"));
            debugInfo.setStyle("-fx-text-fill: #ff0000;");
            return;
        }
        Pair<Pair<Participant, Participant>, String> pair = undoAction.pop();
        Participant oldParticipant = pair.getKey().getKey();
        Participant newParticipant = pair.getKey().getValue();
        switch (pair.getValue()) {
            case "removal": {
                event.addParticipant(oldParticipant);
                updateCollections(oldParticipant);
                event.updateFields();
                event.setDateOfModification(LocalDateTime.now());
                server.send("/app/events/import", event);
                debugInfo.setStyle("-fx-text-fill: #008c1d;");
                debugInfo.textProperty().unbind();
                switch (Language.getLanguage().getLanguage()) {
                    case "en": {
                        debugInfo.setText("Brought back " + oldParticipant.getName() + "!");
                        break;
                    }
                    case "nl": {
                        debugInfo.setText("Teruggebracht " + oldParticipant.getName() + "!");
                        break;
                    }
                    case "bg": {
                        debugInfo.setText("\u0048\u0430\u043c\u0430\u043b\u0435\u043d\u0430 " +
                                oldParticipant.getName() + "!");
                        break;
                    }
                    case "de": {
                        debugInfo.setText(oldParticipant.getName() + " wurde zur\u00fcckgebracht!");
                        break;
                    }
                }
                refresh();
                return;
            }
            case "update": {
                Participant participant = null;
                if (newParticipant != null) {
                    participant = event.getParticipants().stream()
                            .filter(p -> p.getId() == newParticipant.getId())
                            .findFirst().orElse(null);
                }
                if (participant != null) {
                    participant.setName(oldParticipant.getName());
                    participant.setEmail(oldParticipant.getEmail());
                    participant.setIban(oldParticipant.getIban());
                    participant.setBic(oldParticipant.getBic());
                    event.updateParticipant(oldParticipant);
                    event.setDateOfModification(LocalDateTime.now());
                    server.send("/app/events/update", event);
                    debugInfo.setStyle("-fx-text-fill: #008c1d;");
                    debugInfo.textProperty().unbind();
                    switch (Language.getLanguage().getLanguage()) {
                        case "en": {
                            debugInfo.setText("Rolled back modification of " +
                                    newParticipant.getName() + "!");
                            break;
                        }
                        case "nl": {
                            debugInfo.setText("Teruggedraaide wijziging van " +
                                    newParticipant.getName() + "!");
                            break;
                        }
                        case "bg": {
                            debugInfo.setText("Обратна промна от " +
                                    newParticipant.getName() + "!");
                            break;
                        }
                        case "de": {
                            debugInfo.setText("\u00c4nderung von " +
                                    newParticipant.getName() + " " +
                                    "wurde r\u00fcckg\u00e4ngig gemacht!");
                            break;
                        }
                    }
                    refresh();
                }
            }
        }
    }

    /**
     * function for bringing back Expenses and Debts to a Participant that has been removed
     * but its removal has been undone
     *
     * @param oldParticipant the participant that has been removed
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private void updateCollections(Participant oldParticipant) {
        for (Expense expense : oldParticipant.getExpensesPaid()) {
            for (Participant participant : event.getParticipants()) {
                if (participant.getExpensesInvolved().stream()
                        .noneMatch(e -> e.getUuid().equals(expense.getUuid()))
                        && expense.getParticipants().stream().map(Participant::getId)
                        .anyMatch(id -> id == participant.getId()))
                    participant.getExpensesInvolved().add(expense);
            }
        }
        for (Transaction transaction : oldParticipant.getTransactionsFrom()) {
            event.getParticipants().stream()
                    .filter(p -> p.getId() == transaction.getPayee().getId())
                    .findFirst().ifPresent(p -> p.getTransactionsTo().add(transaction));
        }
        for (Transaction transaction : oldParticipant.getTransactionsTo()) {
            event.getParticipants().stream()
                    .filter(p -> p.getId() == transaction.getPayer().getId())
                    .findFirst().ifPresent(p -> p.getTransactionsFrom().add(transaction));
        }
    }

    /**
     * Refreshes the TableView with the latest data from the server.
     * Clears the TableView and repopulates it with the updated data.
     */
    public void refresh() {
        var participants = event.getParticipants();
        data = FXCollections.observableList(participants);
        table.getItems().setAll(data);
    }

    /**
     * for going back
     */
    public void goBack() {
        clearFields();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Potential Remover setter
     *
     * @param p the participant to be removed
     */
    public void setPotentialRemover(Participant p) {
        potentialRemover = p;
    }

    /**
     * Removes the selected participant .
     */
    public void remove() {
        if (potentialRemover == null) return;
        undoAction.push(new Pair<>(new Pair<>(potentialRemover, null), "removal"));
        remove(potentialRemover);
        potentialRemover = null;
        savedId = null;
        mainCtrl.showAddParticipant(event);
        debugInfo.textProperty().unbind();
        debugInfo.setText(colDelete.getText().toUpperCase());
    }

    /**
     * Handles the confirmation of removing a Participant.
     * <p>
     * Technology explanation:
     * - The participant and associated expenses they paid for are removed
     * from the local event variable.
     * - The event is sent to the server.
     * - The event and its collections are updated in the database.
     * - The entities in the event's collections are correspondingly added, updated, and deleted.
     * - The server sends the updated event to all clients.
     *
     * @param toBeRemoved the participant to be removed
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    public void remove(Participant toBeRemoved) {
        if (toBeRemoved == null) {
            debugInfo.setStyle("-fx-text-fill: #ff0000;");
            debugInfo.textProperty().bind(Language.createStringBinding("failedToRemove"));
            return;
        } else {
            try {
                event.removeParticipant(toBeRemoved);
                for (Expense expense : toBeRemoved.getExpensesPaid()) {
                    event.removeExpense(expense);
                    for (Participant participant : event.getParticipants()) {
                        participant.getExpensesInvolved().remove(expense);
                    }
                }
                for (Transaction transaction : toBeRemoved.getTransactionsFrom()) {
                    transaction.getPayee().getTransactionsTo().remove(transaction);
                }
                for (Transaction transaction : toBeRemoved.getTransactionsTo()) {
                    transaction.getPayer().getTransactionsFrom().remove(transaction);
                }
                event.setDateOfModification(LocalDateTime.now());
                server.send("/app/events/update", event);
                debugInfo.setStyle("-fx-text-fill: #008c1d;");
                debugInfo.textProperty().unbind();
                switch (Language.getLanguage().getLanguage()) {
                    case "en": {
                        debugInfo.setText("Successful removal of : "
                                + toBeRemoved.getName() + " !");
                        break;
                    }
                    case "nl": {
                        debugInfo.setText("Succesvolle verwijdering van : "
                                + toBeRemoved.getName() + " !");
                        break;
                    }
                    case "bg": {
                        debugInfo.setText("\u0423\u0063\u043f\u0435\u0448\u043d\u043e\u0020" +
                                "\u043e\u0442\u0063\u0442\u0070\u0430\u043d" +
                                "\u0072\u0432\u0430\u043d\u0435\u0020\u043d\u0430 : "
                                + toBeRemoved.getName() + " !");
                        break;
                    }
                    case "de": {
                        debugInfo.setText("Erfolgreiche Entfernung von : "
                                + toBeRemoved.getName() + " !");
                        break;
                    }
                }
            } catch (WebApplicationException e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                debugInfo.setStyle("-fx-text-fill: #ff0000;");
                debugInfo.textProperty().bind(Language.createStringBinding("failedToRemove"));
                alert.showAndWait();
                return;
            }
        }
        clearFields();
    }

    private Participant getParticipant() {
        return new Participant(
                nameField.getText(),
                emailField.getText(),
                event,
                ibanField.getText(),
                bicField.getText());
    }

    /**
     * getter for debug info
     *
     * @return the debug info field text
     */
    public String getDebugInfo() {
        return debugInfo.getText();
    }

    private void clearFields() {
        savedId = null;
        potentialRemover = null;
        nameField.clear();
        emailField.clear();
        ibanField.clear();
        bicField.clear();
        debugInfo.textProperty().unbind();
        debugInfo.setText("");
        table.getSelectionModel().clearSelection();
    }

    /**
     * Handles key events, such as pressing Enter or Escape keys.
     * Calls the corresponding methods (add() or cancel()) based on the pressed key.
     *
     * @param e the KeyEvent object representing the key event
     */
    public void keyPressed(KeyEvent e) throws CloneNotSupportedException {
        switch (e.getCode()) {
            case ENTER:
                add();
                break;
            case ESCAPE:
                goBack();
                break;
            case R:
                refresh();
                break;
        }
    }

    /**
     * Returns the scene of the AddParticipantCtrl.
     *
     * @return the scene of the AddParticipantCtrl
     */
    public Scene getScene() {
        return table.getScene();
    }

    /**
     * name TextField getter
     *
     * @return the name
     */
    public TextField getNameField() {
        return nameField;
    }

    /**
     * email TextField getter
     *
     * @return the getter
     */
    public TextField getEmailField() {
        return emailField;
    }

    /**
     * iban TextField getter
     *
     * @return the iban
     */
    public TextField getIbanField() {
        return ibanField;
    }

    /**
     * bic TextField getter
     *
     * @return the bic
     */
    public TextField getBicField() {
        return bicField;
    }

    /**
     * Sends test mail for the provided user email
     */
    public void sendTestMail(){
        String email = emailField.getText();
        if(email.isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("The email field is empty");
            errorAlert.setContentText("Please Enter an email address!");
            errorAlert.showAndWait();
        }
        try {
            String sub = "Test mail";
            String bod = "This email is a test whether your provided email address works";
            server.sendMail(email, sub, bod);
            Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmAlert.setHeaderText("Action completed!");
            confirmAlert.setContentText("Test Email has been sent!!!");
            confirmAlert.showAndWait();
        } catch (WebApplicationException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Provided email address does not exist");
            errorAlert.showAndWait();
        }
    }
}