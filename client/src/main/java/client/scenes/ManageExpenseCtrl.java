package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class ManageExpenseCtrl extends WebSocketConnector {

    private Map<String, String> types;
    private Map<String, String> allTypes;
    private List<HBox> boxes;
    private List<Participant> chosenParticipants;
    private Expense expense;

    @FXML
    private VBox group;
    @FXML
    private ScrollPane participantsScroll;
    @FXML
    private Group participantsGroup;
    @FXML
    private TextField title;
    @FXML
    private ComboBox<String> payer;

    private List<String> payerNames;
    private List<String> payerEmails;

    @FXML
    private TextField amount;
    @FXML
    private ComboBox<String> currency;
    @FXML
    private DatePicker date;
    @FXML
    private CheckBox equally;
    @FXML
    private CheckBox onlySome;
    @FXML
    private ComboBox<String> expenseType;
    @FXML
    private TextField newExpenseType;
    @FXML
    private Label expenseTypeLabel;
    @FXML
    private Button add;
    @FXML
    private Button back;
    @FXML
    private Button save;
    @FXML
    private Button delete;

    @FXML
    private Label titleLabel;
    @FXML
    private Label whoPaid;
    @FXML
    private Label whatFor;
    @FXML
    private Label howMuch;
    @FXML
    private Label when;
    @FXML
    private Label howToSplit;

    /**
     * Constructs an instance of ManageExpenseCtrl with the specified dependencies.
     *
     * @param server   the ServerUtils instance
     *                 used for server communication for Expense management
     * @param mainCtrl the MainCtrl instance used for controlling the main application flow
     */
    @Inject
    public ManageExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
        this.chosenParticipants = null;
    }

    /**
     * Initializes the ManageExpenseCtrl with a WebSocket connection.
     * Fills out fields if updating an expense.
     */
    @FXML
    public void initialize() {
        this.date.setEditable(false);
        this.allTypes = new HashMap<>();
        this.boxes = new ArrayList<>();
        this.chosenParticipants = new ArrayList<>();
        this.payerNames = new ArrayList<>();
        this.payerEmails = new ArrayList<>();
        if (this.expense != null) {
            setExpenseFields();
        }
        this.participantsScroll.setVisible(false);
        this.expenseType.setItems(FXCollections
                .observableArrayList("food", "entrance fees", "travel"));
        this.types = new HashMap<>();
        this.currency.getItems().addAll("EUR");

        titleLabel.textProperty().bind(Language.createStringBinding("manageExpenseTitle"));
        whoPaid.textProperty().bind(Language.createStringBinding("manageExpenseWhoPaid"));
        whatFor.textProperty().bind(Language.createStringBinding("manageExpenseWhatFor"));
        howMuch.textProperty().bind(Language.createStringBinding("manageExpenseHowMuch"));
        when.textProperty().bind(Language.createStringBinding("manageExpenseWhen"));
        howToSplit.textProperty().bind(Language.createStringBinding("manageExpenseHowToSplit"));
        equally.textProperty().bind(Language.createStringBinding("manageExpenseEqually"));
        onlySome.textProperty().bind(Language.createStringBinding("manageExpenseOnlySome"));
        expenseTypeLabel.textProperty().bind(Language.createStringBinding
                ("manageExpenseExpenseType"));
        newExpenseType.promptTextProperty().bind(Language.createStringBinding
                ("manageExpenseEnterNewExpenseType"));
        back.textProperty().bind(Language.createStringBinding("manageExpenseBack"));
        delete.textProperty().bind(Language.createStringBinding("manageExpenseDelete"));
        save.textProperty().bind(Language.createStringBinding("manageExpenseSave"));
    }

    private void setExpenseFields() {
        this.boxes.clear();
        this.types.putAll(this.expense.getExpenseTypes());
        this.chosenParticipants = this.expense.getParticipants();
        this.payer.setValue(participantRepresentation(this.expense.getPayer()));
        this.title.setText(this.expense.getTitle());
        this.amount.setText(String.format(Locale.US, "%.2f", this.expense.getAmount()));
        this.currency.setValue(this.expense.getCurrency());
        this.date.setValue(this.expense.getDate());
        if (this.expense.isSplitEqually()) {
            this.equally.setSelected(true);
        } else {
            this.onlySome.setSelected(true);
            showChosenParticipants();
        }
        for (String entry : this.types.keySet()) {
            Label typeLabel = new Label(entry);
            HBox hbox = new HBox(6);
            hbox.getChildren().add(typeLabel);
            boxes.add(hbox);
            displayExpenseTypeLabel(typeLabel, hbox);
        }
    }

    /**
     * Sets the expense for the ManageExpenseCtrl
     *
     * @param expense the expense to set
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * Cancels the operation of managing an expense.
     * Clears the fields and navigates back to the overview scene.
     */
    public void cancel() {
        mainCtrl.showEventOverview(event);
        clearFields();
    }

    /**
     * clears all fields in the manage expense view
     */
    public void clearFields() {
        this.types.clear();
        this.title.clear();
        this.amount.clear();
        this.newExpenseType.clear();
        if (expense == null) {
            showSplitEqually();
        } else {
            if (expense.isSplitEqually()) {
                showSplitEqually();
            } else {
                showChosenParticipants();
            }
        }
        this.payer.getSelectionModel().clearSelection();
        this.currency.getSelectionModel().clearSelection();
        this.expenseType.getSelectionModel().clearSelection();
        this.expenseType.getItems().clear();
        allTypes = event.getExpenses().stream()
                .map(Expense::getExpenseTypes)
                .flatMap(map -> map.entrySet().stream())
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.expenseType.getItems().addAll((FXCollections.observableArrayList(allTypes
                .keySet())));
        for (String tag : List.of("food", "entrance fees", "travel")) {
            if (!allTypes.containsKey(tag)) {
                allTypes.put(tag, colorToHexCode(getRandomColor()));
                this.expenseType.getItems().add(tag);
            }
        }

        this.date.getEditor().clear();
        this.date.setValue(null);
        for (HBox box : boxes) {
            box.setVisible(false);
        }
        this.group.getChildren().clear();
        this.boxes.clear();
        this.participantsScroll.setContent(null);
        this.participantsScroll.setVisible(false);
    }

    /**
     * Refreshes the data displayed in the expense type combo
     * box as well as the event to which the expense is related
     * Sets the Event for this expense.
     *
     * @param e  event to pass
     * @param ex expense to pass
     */
    public void refresh(Event e, Expense ex) {
        if (this.expense != null) {
            setExpenseFields();
        } else {
            this.event = e;
            this.chosenParticipants = e.getParticipants();
            this.expense = ex;
            this.payer.setItems(FXCollections.observableArrayList(e.getParticipants()
                    .stream().map(this::participantRepresentation).toList()));
            this.payerNames = new ArrayList<>(e.getParticipants().stream()
                    .map(Participant::getName).toList());
            this.payerEmails = new ArrayList<>(e.getParticipants().stream()
                    .map(Participant::getEmail).toList());
        }
    }

    /**
     * Refreshes the data displayed in the expense type combo
     * box as well as the event to which the expense is related
     * Sets the Event for this expense.
     */
    public void refresh() {
        this.chosenParticipants = event.getParticipants();
        this.payer.setItems(FXCollections.observableArrayList(event.getParticipants()
                .stream().map(this::participantRepresentation).toList()));
        this.payerNames = new ArrayList<>(event.getParticipants().stream()
                .map(Participant::getName).toList());
        this.payerEmails = new ArrayList<>(event.getParticipants().stream()
                .map(Participant::getEmail).toList());
    }

    /**
     * Returns the equally checkbox
     *
     * @return the equally checkbox
     */
    public CheckBox getEqually() {
        return equally;
    }

    /**
     * Returns the onlySome checkbox
     *
     * @return the onlySome checkbox
     */
    public CheckBox getOnlySome() {
        return onlySome;
    }

    /**
     * Returns a text representation of a participant showing their name and email
     *
     * @param p the participant we want to show
     * @return the text representation of the given participant
     */
    public String participantRepresentation(Participant p) {
        String email = p.getEmail();
        if (Participant.uuidValidator(email)) {
            int index = event.getParticipants().indexOf(p) + 1;
            return p.getName() + " (" + index + ")";
        } else {
            return p.getName() + " (" + email + ")";
        }
    }

    /**
     * <p>
     * Handles the confirmation of adding or editing an expense.
     * Attempts to add the expense to the server, displaying an error alert if an exception occurs.
     * Clears the fields and navigates back to the eventOverview scene upon successful addition.
     * <p>
     * Technology explanation:
     * If adding an expense:
     * - The expenses is added to the local event variable.
     * If updating an expense:
     * - The expenses is updated in the local event variable and participant collections.
     * <p>
     * - The event is sent to the server.
     * - The event and its collections are updated in the database.
     * - The entities in the event's collections are correspondingly added, updated, and deleted.
     * - The server sends the updated event to all clients.
     * - The event overview scene is displayed with the changed event.
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    public void ok() {
        try {
            if (expense != null) {
                long expenseId = expense.getId();
                for (Participant p : event.getParticipants()) {
                    p.getExpensesPaid().remove(expense);
                    p.getExpensesInvolved().remove(expense);
                }
                Expense updatedExpense = getExpense();
                if (updatedExpense == null) {
                    return;
                }
                if (this.chosenParticipants.isEmpty()) {
                    throw new WebApplicationException(
                            "You must choose at least one participant.");
                }
                updatedExpense.setId(expenseId);
                updatedExpense.setUuid(expense.getUuid());
                updatedExpense.getPayer().addExpensePaid(updatedExpense);
                for (Participant p : this.chosenParticipants) {
                    p.addExpenseInvolved(updatedExpense);
                }
                event.updateExpense(updatedExpense);
            } else {
                List<Participant> participants = event.getParticipants();
                Expense expense = getExpense();
                if (expense == null) {
                    return;
                }
                if (this.chosenParticipants.isEmpty()) {
                    throw new WebApplicationException(
                            "You must choose at least one participant.");
                }
                event.addExpense(expense);
                participants.stream().filter(p -> p.getId() == expense.getPayer()
                        .getId()).findFirst().ifPresent(p -> p.addExpensePaid(expense));
                for (Participant p : this.chosenParticipants) {
                    participants.stream().filter(participant -> participant.getId() == p.getId())
                            .findFirst().ifPresent(participant ->
                                    participant.addExpenseInvolved(expense));
                }
                event.setParticipants(participants);
            }
            event.setDateOfModification(LocalDateTime.now());
            server.send("/app/events/update", event);
            mainCtrl.showEventOverview(event);
            clearFields();
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Deletes an expense from an event and returns to the event page
     * Technology explanation:
     * - The expense is removed from the local event variable.
     * - The event is sent to the server.
     * - The event and its collections are updated in the database.
     * - The entities in the event's collections are correspondingly added, updated, and deleted.
     * - The server sends the updated event to all clients.
     * - The event overview scene is displayed with the changed event.
     */
    public void delete() {
        event.removeExpense(expense);
        for (Participant p : event.getParticipants()) {
            p.getExpensesPaid().remove(expense);
            p.getExpensesInvolved().remove(expense);
        }
        event.updateFields();
        event.setDateOfModification(LocalDateTime.now());
        server.send("/app/events/update", event);
        mainCtrl.showEventOverview(event);
        clearFields();
    }

    /**
     * Handles key events, such as pressing Enter or Escape keys.
     * Calls the corresponding methods (ok() or cancel()) based on the pressed key.
     *
     * @param e the KeyEvent object representing the key event
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER -> ok();
            case ESCAPE -> cancel();
        }
    }

    /**
     * Returns the amount
     *
     * @return the amount
     */
    public TextField getAmount() {
        return amount;
    }

    /**
     * Sets the payer
     *
     * @param payer the payer to set
     */
    public void setPayer(ComboBox<String> payer) {
        this.payer = payer;
    }

    /**
     * Returns an expense from an event with the given invite code
     *
     * @return the searched expense
     */
    public Expense getExpense() {
        double numericAmount = 0;
        try {
            numericAmount = Double.parseDouble(this.amount.getText());
            if (isZeroOrNegative(numericAmount)) return null;
            if (threeOrMoreDecimalPlaces(numericAmount)) return null;
        } catch (Exception e) {
            this.amount.clear();
            Alert incorrectFormatAlert = new Alert(Alert.AlertType.ERROR);
            incorrectFormatAlert.initModality(Modality.APPLICATION_MODAL);
            incorrectFormatAlert.titleProperty().bind(Language.createStringBinding("careful"));
            incorrectFormatAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            incorrectFormatAlert.setContentText("Putting something that is not a " +
                    "number as an expense amount.");
            incorrectFormatAlert.showAndWait();
            return null;
        }

        int indexOfSelection = this.payer.getSelectionModel().getSelectedIndex();
        Optional<Participant> part = Optional.empty();
        if (indexOfSelection != -1) {
            part = event.getParticipants().
                    stream().filter(p -> p.getName().equals(this.payerNames.get(indexOfSelection))
                            && p.getEmail().equals(this.payerEmails.get(indexOfSelection)))
                    .findFirst();
        }
        if (indexOfSelection == -1 || part.isEmpty() || title.getText().isEmpty()) {
            Alert incorrectFormatAlert = new Alert(Alert.AlertType.ERROR);
            incorrectFormatAlert.initModality(Modality.APPLICATION_MODAL);
            incorrectFormatAlert.titleProperty().bind(Language.createStringBinding("careful"));
            incorrectFormatAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            incorrectFormatAlert
                    .setContentText("You left a required field empty.");
            incorrectFormatAlert.showAndWait();
            return null;
        }
        Participant payer = part.get();
        return new Expense(event, payer, this.chosenParticipants, title.getText(),
                numericAmount, currency.getValue(), date.getValue(),
                this.equally.isSelected(), this.types);
    }

    private boolean threeOrMoreDecimalPlaces(double numericAmount) {
        String text = Double.toString(Math.abs(numericAmount));
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;

        if (decimalPlaces > 2) {
            Alert incorrectFormatAlert = new Alert(Alert.AlertType.ERROR);
            incorrectFormatAlert.initModality(Modality.APPLICATION_MODAL);
            incorrectFormatAlert.titleProperty().bind(Language.createStringBinding("careful"));
            incorrectFormatAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            incorrectFormatAlert.setContentText("The number cannot have more than two " +
                    "decimal places.");
            incorrectFormatAlert.showAndWait();
            return true;
        }
        return false;
    }

    private boolean isZeroOrNegative(double numericAmount) {
        if (numericAmount <= 0) {
            Alert incorrectFormatAlert = new Alert(Alert.AlertType.ERROR);
            incorrectFormatAlert.initModality(Modality.APPLICATION_MODAL);
            incorrectFormatAlert.titleProperty().bind(Language.createStringBinding("careful"));
            incorrectFormatAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            incorrectFormatAlert.setContentText("The number cannot be zero or negative.");
            incorrectFormatAlert.showAndWait();
            return true;
        }
        return false;
    }

    /**
     * Gets the payer names
     *
     * @return the payer names
     */
    public List<String> getPayerNames() {
        return payerNames;
    }

    /**
     * Gets the payer emails
     *
     * @return the payer emails
     */
    public List<String> getPayerEmails() {
        return payerEmails;
    }

    /**
     * Returns the title field
     *
     * @return the title field
     */
    public TextField getTitle() {
        return title;
    }

    /**
     * Adds a new label displaying every chosen option
     * and allows the removal of an already chosen option
     */
    public void addExpenseType() {
        if (!this.types.containsKey(this.expenseType.getValue())) {
            this.types.put(this.expenseType.getValue(),
                    this.allTypes.get(this.expenseType.getValue()));
            Label newType = new Label();
            newType.setText(this.expenseType.getValue());
            HBox newTypeBox = new HBox(6);
            newTypeBox.getChildren().add(newType);
            this.boxes.add(newTypeBox);
            displayExpenseTypeLabel(newType, newTypeBox);
            if (this.expense != null) {
                this.expense.setExpenseTypes(types);
            }
        } else {
            Alert sameTypeAlert = new Alert(Alert.AlertType.WARNING);
            sameTypeAlert.initModality(Modality.APPLICATION_MODAL);
            sameTypeAlert.titleProperty().bind(Language.createStringBinding("careful"));
            sameTypeAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            sameTypeAlert.setContentText("Adding the same expense type more than once.");
            sameTypeAlert.showAndWait();
        }
    }

    /**
     * Returns the list of expense types
     *
     * @return the list of expense types
     */
    public Map<String, String> getTypes() {
        return types;
    }

    /**
     * Creates a remove-image and places it in its respective HBox container
     *
     * @param url   the url of the image
     * @param width the width of the image
     * @return a new ImageView if the url for the image is valid
     */
    public ImageView setImage(String url, double width) {
        Image removeImage = new Image(url);
        ImageView imageDisplay = new ImageView();
        imageDisplay.setImage(removeImage);
        imageDisplay.setFitWidth(width);
        imageDisplay.setPreserveRatio(true);
        imageDisplay.setCursor(Cursor.HAND);
        return imageDisplay;
    }

    /**
     * Sets default border for expense type container
     *
     * @param container the container to which we set border
     */
    public void setBorders(HBox container) {
        Insets insets = new Insets(2, 5, 2, 5);
        container.setPadding(insets);

        Paint color = Color.web("black");
        BorderStrokeStyle style = BorderStrokeStyle.SOLID;
        CornerRadii radii = new CornerRadii(20);
        BorderWidths widths = new BorderWidths(1);

        BorderStroke borderStroke = new BorderStroke(color, style, radii, widths);
        Border border = new Border(borderStroke);
        container.setBorder(border);
    }

    /**
     * Sets background color of given HBox
     *
     * @param container the HBox to set background color of
     */
    public void setBackgroundColor(HBox container) {
        Color backGround;
        String labelName = ((Label) container.getChildren().get(0)).getText();
        if (allTypes
                .containsKey(labelName)) {
            String currentColor = allTypes.get(labelName);
            backGround = Color.web(currentColor);
        } else {
            backGround = getRandomColor();
        }

        BackgroundFill backgroundFill = new BackgroundFill(backGround,
                new CornerRadii(20), new Insets(0, 0, 0, 0));
        Background containerBackground = new Background(backgroundFill);
        container.setBackground(containerBackground);
    }

    /**
     * Returns a random color
     *
     * @return a random color
     */
    public static Color getRandomColor() {
        Color backGround;
        int redValue = (int) Math.floor(Math.random() * 256);
        int blueValue = (int) Math.floor(Math.random() * 256);
        int greenValue = (int) Math.floor(Math.random() * 256);
        backGround = Color.rgb(redValue, blueValue, greenValue);
        return backGround;
    }

    /**
     * Checks if background color of the chosen container is bright
     *
     * @param container the container whose background color we check
     * @return true if the color is bright, false otherwise
     */
    public boolean isColorBright(HBox container) {
        BackgroundFill containerFill = container.getBackground().getFills().get(0);
        Color containerColor = (Color) containerFill.getFill();
        double average = (containerColor.getRed() +
                containerColor.getBlue() + containerColor.getGreen()) / 3.0;
        return average > 0.5;
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
     * Displays a new label showing a chosen expense type
     *
     * @param newType   the label to be displayed
     * @param container the container where we store the HBox
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void displayExpenseTypeLabel(Label newType,
                                        HBox container) {
        if (this.boxes.size() > 6) {
            Alert maxNumberOfTags = new Alert(Alert.AlertType.WARNING);
            maxNumberOfTags.initModality(Modality.APPLICATION_MODAL);
            maxNumberOfTags.titleProperty().bind(Language.createStringBinding("careful"));
            maxNumberOfTags.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            maxNumberOfTags.
                    setContentText("Adding more than 6 tags per expense.");
            maxNumberOfTags.showAndWait();
            return;
        }
        newType.setTextFill(Color.BLACK);
        newType.setFont(new Font(16));
        container.setFillHeight(false);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setMinWidth(30);
        container.setId("expenseTypeContainer");
        Button removeButton = new Button();
        ImageView imageView = setImage("/images/delete.png", 18);

        setBorders(container);
        setBackgroundColor(container);
        if (isColorBright(container)) {
            newType.setTextFill(Color.web("black"));
        } else {
            newType.setTextFill(Color.web("white"));
            imageView = setImage("/images/delete-white.png", 18);
        }

        Color setColor = (Color) container.getBackground().getFills().get(0).getFill();
        String hex = colorToHexCode(setColor);
        types.put(newType.getText(), hex);

        if (imageView != null) {
            removeButton.setGraphic(imageView);
        }
        removeButton.setFocusTraversable(false);
        removeButton.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: transparent; -fx-focus-color: transparent;");
        container.getChildren().add(removeButton);

        if (group.getChildren().isEmpty() || ((HBox) (group.getChildren()
                .get(group.getChildren().size() - 1)))
                .getChildren().size() == 4) {
            HBox innerBox = new HBox(10);
            innerBox.getChildren().add(new Group(container));
            group.getChildren().add(innerBox);
        } else {
            HBox lastInnerBox = (HBox) group.getChildren().get(group.getChildren().size() - 1);
            lastInnerBox.getChildren().add(new Group(container));
        }
        Parent parent = newType.getParent();
        if (parent.getChildrenUnmodifiable().size() == 1) {
            return;
        }
        container.getChildren().get(1).setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Optional<HBox> optionalRemovedBox = boxes.stream().
                        filter(b -> ((Label) b.getChildren().get(0))
                                .getText().equals(newType.getText())).findFirst();
                HBox removedBox = optionalRemovedBox.get();
                HBox parentBox = (HBox) removedBox.getParent().getParent();

                parentBox.getChildren().removeIf(node -> {
                    HBox hbox = (HBox) ((Group) node).getChildren().get(0);
                    Label label = (Label) hbox.getChildren().get(0);
                    return label.getText()
                            .equals(((Label) removedBox.getChildren().get(0)).getText());
                });
                newType.getParent().setVisible(false);
                boxes.remove(removedBox);
                types.remove(newType.getText());
            }
        });

        container.getChildren().get(0).setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String labelText = ((Label) container.getChildren().get(0)).getText();
                if (expense == null || !allTypes.containsKey(labelText)) {
                    Alert notFilledAlert = new Alert(Alert.AlertType.ERROR);
                    notFilledAlert.initModality(Modality.APPLICATION_MODAL);
                    notFilledAlert.titleProperty().bind(Language.createStringBinding("careful"));
                    notFilledAlert.headerTextProperty().bind(Language
                            .createStringBinding("cannotPerformThisOperation"));
                    notFilledAlert.
                            setContentText("Editing expense types before saving the expense");
                    notFilledAlert.showAndWait();
                    return;
                }
                Color color = (Color) container.getBackground().getFills().get(0).getFill();
                AbstractMap.SimpleEntry<String, String> entry =
                        new AbstractMap.SimpleEntry<>(labelText, colorToHexCode(color));
                clearFields();
                mainCtrl.showEditExpenseType(event, expense,
                        entry);
            }
        });
    }


    /**
     * Makes the group of participant combo boxes invisible and clears the other choice
     */
    public void showSplitEqually() {
        this.equally.setSelected(true);
        this.onlySome.setSelected(false);
        this.participantsGroup.setVisible(false);
        this.participantsGroup.setFocusTraversable(false);
        if (event != null) {
            this.chosenParticipants = event.getParticipants();
        }
        this.participantsScroll.setContent(null);
        this.participantsScroll.setVisible(false);
    }

    /**
     * Gives the user the option to choose certain participant to split the expense amount
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void showChosenParticipants() {
        this.participantsScroll.setVisible(true);
        this.participantsGroup.setVisible(true);
        this.participantsGroup.setFocusTraversable(false);
        this.equally.setSelected(false);
        this.chosenParticipants = new ArrayList<>();
        if (this.expense != null) {
            this.chosenParticipants = expense.getParticipants();
        }
        double lastCheckboxY = 0;
        participantsGroup.getChildren().clear();
        for (Participant p : event.getParticipants()) {
            CheckBox participantBox = new CheckBox(participantRepresentation(p));
            participantBox.setFocusTraversable(false);
            participantBox.setFont(new Font("Arial", 14));
            participantBox.setLayoutY(lastCheckboxY);
            participantBox.setVisible(true);
            if (this.chosenParticipants.contains(p)) {
                participantBox.setSelected(true);
            }
            participantBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    if (!chosenParticipants.contains(p)) {
                        chosenParticipants.add(p);
                    }
                } else {
                    chosenParticipants.remove(p);
                }
            });
            participantsGroup.getChildren().add(participantBox);
            lastCheckboxY += 30;
        }
        this.participantsScroll.setContent(this.participantsGroup);
    }

    /**
     * Returns the group of participants
     *
     * @return the group of participants
     */
    public Group getParticipantsGroup() {
        return participantsGroup;
    }

    /**
     * Returns the list of chosen participants
     *
     * @return the list of chosen participants
     */
    public List<Participant> getChosenParticipants() {
        return chosenParticipants;
    }


    /**
     * Adding a new expense type through the text box
     */
    public void addNewExpenseType() {
        String type = newExpenseType.getText().toLowerCase();
        if (!type.isEmpty() && !this.types.containsKey(type)) {
            expenseType.getItems().add(type);
            Label newType = new Label(type);
            HBox hbox = new HBox(6);
            hbox.getChildren().add(newType);
            this.boxes.add(hbox);
            displayExpenseTypeLabel(newType, hbox);
            if (expense != null) {
                expense.setExpenseTypes(types);
            }
        } else {
            Alert sameTypeAlert = new Alert(Alert.AlertType.WARNING);
            sameTypeAlert.initModality(Modality.APPLICATION_MODAL);
            sameTypeAlert.titleProperty().bind(Language.createStringBinding("careful"));
            sameTypeAlert.headerTextProperty().bind(Language
                    .createStringBinding("cannotPerformThisOperation"));
            sameTypeAlert.setContentText("Adding the same expense type more than once.");
            sameTypeAlert.showAndWait();
        }
        newExpenseType.clear();
    }

    /**
     * Returns the scene of the controller
     *
     * @return the scene of the controller
     */
    public Scene getScene() {
        return back.getScene();
    }

    /**
     * Getter for all types
     * @return all types
     */
    public Map<String, String> getAllTypes() {
        return allTypes;
    }
}
