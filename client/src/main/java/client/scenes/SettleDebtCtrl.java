package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Debt;
import commons.Event;
import commons.Participant;
import commons.Transaction;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class SettleDebtCtrl extends WebSocketConnector {
    private Debt debt;

    @FXML
    private Spinner<Double> amount;

    @FXML
    private Button cancel;

    @FXML
    private Button confirm;

    @FXML
    private Label enterSum;

    private boolean active;


    /**
     * Constructor for SettleDebtCtrl
     *
     * @param server   the server utility
     * @param mainCtrl the main controller
     */
    @Inject
    public SettleDebtCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
        this.active = false;
    }

    /**
     * Initialize the websocket connection
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    @Override
    public void connectWebSocket() {
        server.registerForUpdatesWS("/topic/events/update", Event.class, e -> {
            if (e.getInviteCode().equals(event.getInviteCode())) {
                event = e;
                event.updateFields();
                if (!event.debtOverview().contains(debt) && active) {
                    Platform.runLater(this::closeOnDebtChange);
                }
            }
        });
        server.registerForUpdatesWS("/topic/events/delete", Event.class, e -> {
            if (event != null && e.getInviteCode().equals(event.getInviteCode())) {
                if (mainCtrl.getPrimaryStage().getScene() == this.getScene()) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Splitty");
                        errorAlert.headerTextProperty().bind(Language.createStringBinding("error"));
                        errorAlert.contentTextProperty()
                                .bind(Language.createStringBinding("eventNoLongerExists"));
                        errorAlert.show();
                        mainCtrl.showStartScreen();
                    });
                }
            }
        });
        server.registerForUpdatesWS("/topic/events/import", Event.class, e -> {
            if (e.getInviteCode().equals(event.getInviteCode())) {
                event = e;
                event.updateFields();
                if (!event.debtOverview().contains(debt) && active) {
                    Platform.runLater(this::closeOnDebtChange);
                }
            }
        });
    }

    /**
     * Refresh method required by the superclass
     */
    public void refresh() {
    }

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        amount.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(-Double.MAX_VALUE,
                        Double.MAX_VALUE, 0));
        enterSum.textProperty().bind(Language.createStringBinding("settleDebtEnterSum"));
        cancel.textProperty().bind(Language.createStringBinding("settleDebtCancel"));
        confirm.textProperty().bind(Language.createStringBinding("settleDebtConfirm"));
    }


    /**
     * Set the event
     *
     * @param event the event
     */
    @Override
    public void setEvent(Event event) {
        this.event = event;
        active = true;
    }

    /**
     * Set the debt
     *
     * @param debt the debt
     */
    public void setDebt(Debt debt) {
        this.debt = debt;
    }

    /**
     * Get the debt
     *
     * @return the debt
     */
    public Debt getDebt() {
        return debt;
    }

    /**
     * Check if the window is active
     *
     * @return true if the window is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Close the window
     */
    @FXML
    public void close() {
        amount.getScene().getWindow().hide();
        amount.getValueFactory().setValue(0.0);
        active = false;
    }

    /**
     * Close the window on debt change and shows error message.
     */
    public void closeOnDebtChange() {
        close();
        Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
        errorAlert.setTitle("Splitty");
        errorAlert.headerTextProperty()
                .bind(Language.createStringBinding("debtsWereUpdated"));
        errorAlert.contentTextProperty()
                .bind(Language.createStringBinding("debtsPleaseTryAgain"));
        errorAlert.showAndWait();
    }

    /**
     * Confirm the transaction
     */
    @FXML
    public void confirm() {
        Transaction transaction = debt.createTransaction(amount.getValue());
        Participant payer = transaction.getPayer();
        Participant payee = transaction.getPayee();
        payer.addTransactionFrom(transaction);
        payee.addTransactionTo(transaction);
        event.setDateOfModification(LocalDateTime.now());
        server.send("/app/events/update", event);
        close();
    }


    /**
     * Get the scene
     *
     * @return the scene
     */
    public Scene getScene() {
        return amount.getScene();
    }


    /**
     * Get the spinner
     *
     * @return the spinner
     */
    public Spinner<Double> getAmount() {
        return amount;
    }


    /**
     * Set the value of the spinner
     *
     * @param value the value
     */
    public void setValue(double value) {
        amount.getValueFactory().setValue(value);
    }

    /**
     * Get the value of the spinner
     *
     * @return the value
     */
    public double getValue() {
        return amount.getValue();
    }
}
