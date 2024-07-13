package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Debt;
import commons.Participant;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class DebtOverviewCtrl extends WebSocketConnector {

    private final ObservableList<Debt> debts;

    private final Map<Debt, String> basicInfo;
    private final Map<Debt, String> paymentInstructions;
    private final Map<Debt, Boolean> showExpand;
    private final Image expandImage;
    private final Image collapseImage;

    @FXML
    private Label title;
    @FXML
    private Button back;

    private static final String LISTVIEW_STYLE = "-fx-text-base-color: black; " +
            "-fx-text-background-color: black; " +
            "-fx-font-size: 14px; -fx-selection-bar: transparent !important; " +
            "-fx-focus-color: transparent !important; -fx-text-fill: black; " +
            "-fx-control-inner-background: transparent; " +
            "-fx-background-color: transparent; -fx-border-color: black; " +
            "-fx-border-width: 1px; -fx-border-radius: 5px;";

    @FXML
    private ListView<Debt> listView;

    /**
     * Constructs an instance of DebtOverviewCtrl with the specified dependencies.
     *
     * @param server   the ServerUtils instance used for server communication
     * @param mainCtrl the MainCtrl instance used for controlling the main application flow
     */
    @Inject
    public DebtOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
        debts = FXCollections.observableArrayList();
        basicInfo = new HashMap<>();
        paymentInstructions = new HashMap<>();
        showExpand = new HashMap<>();
        expandImage = new Image("images/expand.png");
        collapseImage = new Image("images/collapse.png");
    }

    /**
     * Initializes the list view.
     * Sets the cell factory for the list view.
     * Suppresses warnings for the method length due to the complexity of
     * the cell factory and inability to extract methods.
     */
    @SuppressWarnings("methodlength")
    @FXML
    public void initialize() {
        listView.itemsProperty().bind(new SimpleListProperty<>(debts));
        listView.setStyle(LISTVIEW_STYLE);
        listView.setPadding(new Insets(10, 0, 10, 0));
        listView.setCellFactory(lv -> new ListCell<>() {
            private final Button reminderButton = new Button();

            private final Button settleButton = new Button("Settle debt");
            private final Button expandCollapseButton = new Button();
            private final VBox layout = new VBox();
            private final HBox buttonAndLabelBox = new HBox();
            private final VBox paymentInfoBox = new VBox();
            private final HBox paymentInfoButtonBox = new HBox();
            private final Label paymentLabel = new Label();
            private final Label basicInfoLabel = new Label();

            {
                reminderButton.textProperty().bind(Language
                        .createStringBinding("debtOverviewSendReminder"));
                settleButton.textProperty().bind(Language
                        .createStringBinding("debtOverviewSettleDebt"));

                expandCollapseButton.setId("expandCollapseButton");
                settleButton.setId("settleButton");
                reminderButton.setId("reminderButton");

                buttonAndLabelBox.getChildren().addAll(expandCollapseButton, basicInfoLabel);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                paymentInfoButtonBox.getChildren().addAll(reminderButton, spacer, settleButton);
                paymentInfoBox.getChildren().addAll(paymentLabel, paymentInfoButtonBox);
                layout.getChildren().addAll(buttonAndLabelBox, paymentInfoBox);


                layout.setSpacing(0);

                paymentInfoBox.setPadding(new Insets(10, 10, 10, 10));
                paymentInfoBox.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT,
                        new Insets(5, 5, 5, 5))));
                paymentInfoBox.setSpacing(15);

                buttonAndLabelBox.setSpacing(5);
                buttonAndLabelBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setMargin(buttonAndLabelBox, new Insets(0, 0, 0, -5));

                reminderButton.setFocusTraversable(false);
                settleButton.setFocusTraversable(false);

                expandCollapseButton.setFocusTraversable(false);
                expandCollapseButton.setStyle("-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; -fx-focus-color: transparent;");

                reminderButton.setOnMouseReleased(event -> {
                    sendReminder(getItem());
                    event.consume();
                });

                settleButton.setOnMouseReleased(click -> {
                    Debt currentDebt = getItem();
                    if (currentDebt != null) {
                        mainCtrl.showSettleDebt(event, getItem());
                    }
                    click.consume();
                });

                paymentInfoBox.managedProperty().bind(paymentInfoBox.visibleProperty());
                paymentInfoBox.setVisible(false);

                expandCollapseButton.setOnMouseReleased(event -> {
                    Debt currentDebt = getItem();
                    if (currentDebt != null) {
                        ImageView expandCollapse = new ImageView();
                        if (showExpand.containsKey(currentDebt) && showExpand.get(currentDebt)) {
                            showExpand.put(currentDebt, false);
                            expandCollapse.setImage(collapseImage);
                        } else {
                            showExpand.put(currentDebt, true);
                            expandCollapse.setImage(expandImage);
                        }
                        expandCollapse.setFitHeight(15);
                        expandCollapse.setFitWidth(15);
                        expandCollapseButton.setGraphic(expandCollapse);
                        listView.refresh();
                    }
                    event.consume();
                });

                reminderButton.setDisable(true);

                itemProperty().addListener((obs, oldItem, newItem) -> {
                    if (newItem != null) {
                        boolean isUUid = Participant.uuidValidator(System.getProperty("from.email"))
                                || Participant.uuidValidator(newItem.getPayer().getEmail());
                        boolean isNotEmail = !InvitationCtrl.emailValid(
                                System.getProperty("from.email")) ||
                                !InvitationCtrl.emailValid(newItem.getPayer().getEmail());
                        reminderButton.setDisable(isUUid || isNotEmail);
                    } else {
                        reminderButton.setDisable(true);
                    }
                });
            }

            @Override
            public void updateSelected(boolean selected) {
            }

            @Override
            protected void updateItem(Debt item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    basicInfoLabel.setText(basicInfo.get(item));
                    setText(null);
                    setGraphic(layout);
                    ImageView imageView;
                    if (showExpand.containsKey(item) && showExpand.get(item)) {
                        paymentLabel.setText(paymentInstructions.get(item));
                        paymentInfoBox.setVisible(true);
                        imageView = new ImageView(new Image("images/expand.png"));
                        expandCollapseButton.setGraphic(imageView);
                    } else {
                        paymentInfoBox.setVisible(false);
                        imageView = new ImageView(new Image("images/collapse.png"));
                        expandCollapseButton.setGraphic(imageView);
                    }
                    imageView.setFitHeight(15);
                    imageView.setFitWidth(15);
                }
                setStyle("-fx-text-base-color: black; -fx-text-background-color: black; " +
                        "-fx-selection-bar: transparent !important; " +
                        "-fx-focus-color: transparent !important;");
                expandCollapseButton.setStyle("-fx-text-base-color: black; " +
                        "-fx-background-color: transparent; -fx-border-color: transparent; " +
                        "-fx-focus-color: transparent;");
                reminderButton.setStyle("-fx-text-base-color: black;");
                settleButton.setStyle("-fx-text-base-color: black;");
                basicInfoLabel.setStyle("-fx-text-background-color: black;");
                paymentLabel.setStyle("-fx-text-background-color: black;");
            }
        });

        title.textProperty().bind(Language.createStringBinding("debtOverviewTitle"));
        back.textProperty().bind(Language.createStringBinding("debtOverviewBack"));
    }

    private void sendReminder(Debt debt) {
        Participant payer = debt.getPayer();
        String sub = "Payment reminder";
        String bod = "You owe " + debt.getPayee().getName() + " " +
                debt.getTotal() + " " + debt.getCurrency()
                + "\nPlease pay it immediately!";
        try {
            server.sendMail(payer.getEmail(), sub, bod);
            Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmAlert.headerTextProperty().bind(Language
                    .createStringBinding("emailActionCompleted"));
            confirmAlert.contentTextProperty().bind(Language
                    .createStringBinding("emailSent"));
            confirmAlert.showAndWait();
        } catch (WebApplicationException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.headerTextProperty().bind(Language
                    .createStringBinding("emailNotSent"));
            errorAlert.contentTextProperty().bind(Language
                    .createStringBinding("emailDoesNotExist"));
            errorAlert.showAndWait();
        }
    }

    /**
     * Refreshes the list of debts.
     */
    public void refresh() {
        debts.clear();
        basicInfo.clear();
        paymentInstructions.clear();
        showExpand.clear();
        debts.setAll(event.debtOverview());
        for (Debt debt : debts) {
            basicInfo.put(debt, debt.basicInfo(Language.getLanguage().getLanguage()));
            paymentInstructions.put(debt, debt.paymentInstructions(Language
                    .getLanguage().getLanguage()));
            showExpand.put(debt, false);
        }
    }

    /**
     * Gets the basic information of the debts.
     *
     * @return the basic information of the debts
     */
    public Map<Debt, String> getBasicInfo() {
        return basicInfo;
    }

    /**
     * Gets the payment instructions of the debts.
     *
     * @return the payment instructions of the debts
     */
    public Map<Debt, String> getPaymentInstructions() {
        return paymentInstructions;
    }

    /**
     * Gets the show expand status of the debts.
     *
     * @return the show expand status of the debts
     */
    public Map<Debt, Boolean> getShowExpand() {
        return showExpand;
    }

    /**
     * Goes back to the event overview.
     */
    public void goBack() {
        mainCtrl.showEventOverview(event);
    }

    /**
     * Returns the scene of the list view.
     *
     * @return the scene of the list view
     */
    public Scene getScene() {
        return listView.getScene();
    }
}
