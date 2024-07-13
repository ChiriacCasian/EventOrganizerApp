package client.scenes;

import client.utils.Language;
import client.utils.LanguageSelector;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;
import java.util.stream.Collectors;

public class EventOverviewCtrl extends WebSocketConnector implements LanguageSelector {

    private Participant current;

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
    private Label inviteCode;
    @FXML
    private Label overview;
    @FXML
    private Label listOfParticipants;
    @FXML
    private Label debt;
    @FXML
    private ChoiceBox<String> choseParticipant;
    @FXML
    private Button all;
    @FXML
    private Button includingPerson;
    @FXML
    private Button fromPerson;
    @FXML
    private ListView<String> listOfExpenses;
    private Map<Integer, Expense> listOfExpensesMap = new HashMap<>();

    @FXML
    private Button sendInvites;
    @FXML
    private Button addOrRemoveParticipant;
    @FXML
    private Button addExpense;
    @FXML
    private Button settleDebts;
    @FXML
    private Label description;

    @FXML
    private Button back;

    @FXML
    private Button edit;

    @FXML
    private Button statistics;

    @FXML
    private Label participantsLabel;

    @FXML
    private Label expensesLabel;

    private boolean showExpensesFrom;
    private boolean showExpensesIncluding;

    /**
     * Constructs an instance of OverviewCtrl with the specified dependencies.
     *
     * @param server   the ServerUtils instance used for server communication
     * @param mainCtrl the MainCtrl instance used for controlling the main application flow
     */
    @Inject
    public EventOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
        this.current = null;
        this.showExpensesFrom = false;
        this.showExpensesIncluding = false;
    }

    /**
     * Sets the event the controller is associated with.
     *
     * @param event the event.
     */
    @Override
    public void setEvent(Event event) {
        this.event = event;
    }


    /**
     * Initializes the languages
     */
    @FXML
    public void initialize() {
        sendInvites.textProperty().bind(Language.createStringBinding("eventOverviewSendInvites"));
        participantsLabel.textProperty().bind(Language.createStringBinding
                ("eventOverviewParticipants"));
        expensesLabel.textProperty().bind(Language.createStringBinding("eventOverviewExpenses"));
        addOrRemoveParticipant.textProperty().bind(Language.createStringBinding
                ("eventOverviewAddOrRemoveParticipant"));
        addExpense.textProperty().bind(Language.createStringBinding("eventOverviewAddExpense"));
        all.textProperty().bind(Language.createStringBinding("eventOverviewAll"));
        includingPerson.textProperty().bind(Language.createStringBinding
                ("eventOverviewIncludingPerson"));
        fromPerson.textProperty().bind(Language.createStringBinding("eventOverviewFromPerson"));
        back.textProperty().bind(Language.createStringBinding("eventOverviewBack"));
        settleDebts.textProperty().bind(Language.createStringBinding("eventOverviewSettleDebts"));
        statistics.textProperty().bind(Language.createStringBinding("statistics"));

        languageIndicator.setMinSize(flagWidth, flagHeight);
        languageIndicator.setMaxSize(flagWidth, flagHeight);
        languageIndicator.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("styles.css"))
                        .toExternalForm());
        languageIndicator.setStyle("-fx-background-color: transparent;");
    }

    /**
     * Refreshes the scene.
     */
    public void refresh() {
        listOfExpenses.getSelectionModel().clearSelection();
        overview.requestFocus();
        inviteCode.setText(event.getInviteCode());
        overview.setText(event.getTitle());
        listOfParticipants.setText(event.participantsToString());
        choseParticipant.setItems(FXCollections.observableArrayList
                (event.getParticipants().stream().map(Participant::getName).toList()));
        showAllExpenses();
        setCurrent();
        choseExpense();
        description.setText(event.getDescription());
    }


    /**
     * Navigates to the send invites view
     * Calls the showInvitation() method of the MainCtrl to display the send invites scene.
     */
    @FXML
    public void sendInvites() {
        mainCtrl.showInvitation(event);
    }

    /**
     * Navigates to the manage expense view
     * Calls the showManageExpense() method of the MainCtrl to display the manage expenses scene
     */
    public void addExpense() {
        int chosenExpenseIndex = listOfExpenses.getSelectionModel().getSelectedIndex();
        if (chosenExpenseIndex == -1) {
            mainCtrl.showManageExpense(event, null);
        } else {
            Optional<Expense> optionalExpense = event.getExpenses().stream()
                    .filter(ex -> ex.getUuid()
                            .equals(listOfExpensesMap.get(chosenExpenseIndex)
                                    .getUuid())).findFirst();
            listOfExpenses.getSelectionModel().clearSelection();
            if (optionalExpense.isPresent()) {
                mainCtrl.showManageExpense(event, optionalExpense.get());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.titleProperty().bind(Language.createStringBinding("error"));
                alert.headerTextProperty().bind(Language.createStringBinding("expenseNotFound"));
                alert.showAndWait();
            }
        }
    }

    /**
     * Navigates to the debt overview view
     * Calls the showDebtOverview() method of the MainCtrl to display the debt overview scene
     */
    public void settleDebts() {
        mainCtrl.showDebtOverview(event);
    }

    /**
     * Shows the statistics about the event
     */
    public void showStatistics() {
        mainCtrl.showEventStatistics(event);
    }

    /**
     * Listener for the participant choice box
     */
    public void setCurrent() {
        choseParticipant.getSelectionModel()
                .selectedItemProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        if (newValue == null) return;
                        current = event.getParticipants().stream()
                                .filter(x -> x.getName().equals(newValue)).findFirst().get();
                        if (showExpensesFrom) {
                            showExpensesFromParticipant();
                        } else if (showExpensesIncluding) {
                            showExpensesIncludingParticipant();
                        } else {
                            showAllExpenses();
                        }
                    }
                });
    }

    /**
     * Listener for the Expenses list view
     */
    public void choseExpense() {
        listOfExpenses.getSelectionModel()
                .selectedItemProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        if (newValue == null || newValue.equals(oldValue)) return;
                        Expense expense = event.getExpenses().stream()
                                .filter(x -> x.getUuid().equals(listOfExpensesMap
                                        .get(listOfExpenses.getSelectionModel().getSelectedIndex())
                                        .getUuid())).findFirst().get();
                        mainCtrl.showManageExpense(event, expense);
                    }
                });
    }

    /**
     * Sets the list of expenses to show all expenses
     */
    public void showAllExpenses() {
        listOfExpenses.setItems(FXCollections.observableArrayList
                (event.getExpenses().stream().map(Expense::getTitle).toList()));
        listOfExpensesMap = event.getExpenses().stream()
                .collect(Collectors.toMap(e -> event.getExpenses().indexOf(e), e -> e));
        String totalSumString = "";
        switch (Language.getLanguage().getLanguage()) {
            case "en": {
                totalSumString = "Total sum of expenses";
                break;
            }
            case "nl": {
                totalSumString = "Totale som van uitgaven";
                break;
            }
            case "bg": {
                totalSumString = "\u041e\u0431\u0449\u0430\u0020\u0441\u0443" +
                        "\u043c\u0430\u0020\u043d\u0430\u0020\u0440\u0430\u0437" +
                        "\u0445\u043e\u0434\u0438\u0442\u0435";
                break;
            }
            case "de": {
                totalSumString = "Gesamtsumme der Ausgaben";
                break;
            }
        }
        debt.setText(totalSumString + ": " +
                (double) Math.round(100 * event.totalExpenses()) / 100 + " " + event.getCurrency());
    }

    /**
     * Sets the list of expenses to show only the expenses participant p has paid for
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void showExpensesFromParticipant() {
        showExpensesFrom = true;
        showExpensesIncluding = false;
        if (current == null) return;
        ObservableList<String> efp = FXCollections.observableArrayList();
        listOfExpensesMap.clear();
        int i = 0;
        for (Expense expense : event.getExpenses()) {
            if (expense.getPayer().equals(current)) efp.add(expense.getTitle());
            listOfExpensesMap.put(i, expense);
            i++;
        }
        listOfExpenses.setItems(efp);
        double sum = event.getParticipants().stream()
                .mapToDouble(p -> Math.abs(event.calculateShareOwedTo(p))).sum();
        double owedTo = (double) Math.round(100 * event.calculateShareOwedTo(current)) / 100;
        double owedFrom = (double) Math.round(100 * event.calculateShareOwedFrom(current)) / 100;
        if (owedTo < 0) {
            owedTo = 0;
        }
        if (owedFrom < 0) {
            owedTo += -1 * owedFrom;
        }

        String totalOwedString = "";
        switch (Language.getLanguage().getLanguage()) {
            case "en": {
                totalOwedString = "Total owed to ";
                break;
            }
            case "nl": {
                totalOwedString = "Totaal verschuldigd aan ";
                break;
            }
            case "bg": {
                totalOwedString = "\u041e\u0431\u0449\u043e\u0020\u0434" +
                        "\u044a\u043b\u0436\u0438\u043c\u043e\u0020\u043d\u0430";
                break;
            }
            case "de": {
                totalOwedString = "Gesamtschuld an ";
                break;
            }
        }


        debt.setText(totalOwedString + current.getName() + ": "
                + owedTo + " " + event.getCurrency() + " (" +
                Math.round(100 * owedTo / sum) + "%)");
    }

    /**
     * Sets the list of expenses to show only the expenses participant p has to pay for
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public void showExpensesIncludingParticipant() {
        showExpensesFrom = false;
        showExpensesIncluding = true;
        if (current == null) return;
        ObservableList<String> eip = FXCollections.observableArrayList();
        int i = 0;
        for (Expense expense : event.getExpenses()) {
            if (expense.getParticipants().contains(current)) eip.add(expense.getTitle());
            listOfExpensesMap.put(i, expense);
            i++;
        }
        listOfExpenses.setItems(eip);
        double sum = event.getParticipants().stream()
                .mapToDouble(p -> Math.abs(event.calculateShareOwedTo(p))).sum();
        double owedFrom = (double) Math.round(100 * event.calculateShareOwedFrom(current)) / 100;
        double owedTo = (double) Math.round(100 * event.calculateShareOwedTo(current)) / 100;
        if (owedFrom < 0) {
            owedFrom = 0;
        }
        if (owedTo < 0) {
            owedFrom += -1 * owedTo;
        }

        String totalOwedString = "";

        switch (Language.getLanguage().getLanguage()) {
            case "en": {
                totalOwedString = "Total owed by ";
                break;
            }
            case "nl": {
                totalOwedString = "Totaal verschuldigd door ";
                break;
            }
            case "bg": {
                totalOwedString = "\u041e\u0431\u0449\u043e\u0020\u0434" +
                        "\u044a\u043b\u0436\u0438\u043c\u043e\u0020\u043e\u0442";
                break;
            }
            case "de": {
                totalOwedString = "Gesamtschuld von ";
                break;
            }
        }

        debt.setText(totalOwedString + current.getName() + ": "
                + owedFrom + " " + event.getCurrency() + " (" +
                Math.round(100 * owedFrom / sum) + "%)");
    }

    /**
     * Function for the Add/remove button in the Event overview scene
     */
    public void addOrRemoveParticipant() {
        mainCtrl.showAddParticipant(event);
    }

    /**
     * Goes back to Start Screen scene
     */
    @FXML
    public void goBack() {
        mainCtrl.showStartScreen();
    }

    /**
     * Edits the Event title in the scene and in the database
     */
    @FXML
    public void editTitle() {
        mainCtrl.showEditTitle(event);
    }

    /**
     * Handles different key inputs, escape goes back to StartScreen
     *
     * @param e key Event executed
     */
    public void keyPressed(KeyEvent e) {
        if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) {
            goBack();
        }
    }

    /**
     * Returns the scene of the controller
     *
     * @return the scene of the controller
     */
    public Scene getScene() {
        return overview.getScene();
    }

    /**
     * sets graphics of the edit button
     */
    public void setGraphics() {
        ImageView imageView = new
                ImageView(Objects.requireNonNull(getClass()
                .getResource("/images/editPencil.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        imageView.setTranslateY(-4);
        edit.setGraphic(imageView);
        edit.setStyle("-fx-background-color: transparent;");
    }

    /**
     * Returns the edit button
     *
     * @return the edit button
     */
    public Button getEdit() {
        return edit;
    }

    /**
     * Returns the debt label
     *
     * @return the debt label
     */
    public Label getDebt() {
        return debt;
    }

    /**
     * Returns the list of expenses listview
     *
     * @return the list of expenses listview
     */
    public ListView<String> getListOfExpenses() {
        return listOfExpenses;
    }

    /**
     * Sets the language
     *
     * @param locale the locale
     */
    public void setLanguage(Locale locale) {
        Language.setLanguage(locale);
        setLanguageButtonGraphic();
        if (event != null) {
            if (showExpensesFrom) {
                showExpensesFromParticipant();
            } else if (showExpensesIncluding) {
                showExpensesIncludingParticipant();
            } else {
                showAllExpenses();
            }
        }
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
