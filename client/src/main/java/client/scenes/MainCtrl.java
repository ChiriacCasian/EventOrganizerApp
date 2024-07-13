/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.scenes;

import commons.Debt;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class MainCtrl {

    private Stage primaryStage;
    private Stage secondaryStage;

    private EditTitleCtrl editTitleCtrl;

    private Scene editTitle;

    private EditExpenseTypeCtrl editExpenseTypeCtrl;
    private Scene editExpenseType;

    private ManageExpenseCtrl manageExpenseCtrl;
    private Scene manageExpense;

    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverview;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    private InvitationCtrl invitationCtrl;
    private Scene invitation;

    private AddParticipantCtrl addParticipantCtrl;
    private Scene addParticipant;
    private AdminOverviewCtrl adminOverviewCtrl;
    private Scene adminOverview;

    private LoginCtrl loginCtrl;
    private Scene loginPage;

    private DebtOverviewCtrl debtOverviewCtrl;
    private Scene debtOverview;

    private SettleDebtCtrl settleDebtCtrl;
    private Scene settleDebt;
    private Scene acceptDelete;
    private EventStatisticsCtrl eventStatisticsCtrl;
    private Scene eventStatistics;
    private List<Event> recentEvents;


    /**
     * Initializes the application by setting overview scene, and add scene.
     *
     * @param primaryStage    the primary stage
     * @param manageExpense   a Pair containing the controller and Parent node for the manage
     *                        expenses scene
     * @param eventOverview   a Pair containing the controller and Parent node for the event
     *                        overview scene
     * @param startScreen     a Pair containing the controller and Parent node for the event
     *                        overview scene
     * @param invitation      a Pair containing the controller and Parent node for the invitation
     * @param addParticipant  a Pair containing the controller and Parent node for the add
     *                        participant scene
     * @param adminOverview   a Pair containing the controller and Parent node for the
     *                        admin overview scene
     * @param loginPage       a Pair containing the controller and Parent node for the login page
     *                        scene
     * @param editTitle       a Pair containing the controller and Parent node for the edit title
     *                        scene
     * @param editExpenseType a Pair containing the controller and Parent node for the edit expense
     *                        type scene
     * @param debtOverview    a Pair containing the controller and Parent node for debt overview
     * @param settleDebt      a Pair containing the controller and Parent node for the settle debt
     *                        scene
     * @param acceptDelete    a Pair containing the controller and Parent node for
     *                        the accept delete scene
     * @param eventStatistics a Pair containing the controller and Parent node for
     *                        the event statistics scene
     */
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:MethodLength"})
    public void initialize(Stage primaryStage,
                           Pair<ManageExpenseCtrl, Parent> manageExpense,
                           Pair<EventOverviewCtrl, Parent> eventOverview,
                           Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<InvitationCtrl, Parent> invitation,
                           Pair<AddParticipantCtrl, Parent> addParticipant,
                           Pair<AdminOverviewCtrl, Parent> adminOverview,
                           Pair<LoginCtrl, Parent> loginPage,
                           Pair<EditTitleCtrl, Parent> editTitle,
                           Pair<SettleDebtCtrl, Parent> settleDebt,
                           Pair<EditExpenseTypeCtrl, Parent> editExpenseType,
                           Pair<AddParticipantCtrl, Parent> acceptDelete,
                           Pair<DebtOverviewCtrl, Parent> debtOverview,
                           Pair<EventStatisticsCtrl, Parent> eventStatistics) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Splitty");

        secondaryStage = new Stage();
        secondaryStage.initOwner(primaryStage);
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.setTitle("Splitty");

        this.manageExpenseCtrl = manageExpense.getKey();
        this.manageExpense = new Scene(manageExpense.getValue());
        this.manageExpense.getStylesheets().add(getClass()
                .getResource("styles.css").toExternalForm());

        this.editTitleCtrl = editTitle.getKey();
        this.editTitle = new Scene(editTitle.getValue());

        this.editExpenseTypeCtrl = editExpenseType.getKey();
        this.editExpenseType = new Scene(editExpenseType.getValue());

        this.acceptDelete = new Scene(acceptDelete.getValue());

        this.eventOverviewCtrl = eventOverview.getKey();
        this.eventOverview = new Scene(eventOverview.getValue());
        this.eventOverviewCtrl.setFlags();
        this.eventOverviewCtrl.setGraphics();

        this.startScreenCtrl = startScreen.getKey();
        this.startScreen = new Scene(startScreen.getValue());
        this.startScreenCtrl.setFlags();

        this.invitationCtrl = invitation.getKey();
        this.invitation = new Scene(invitation.getValue());

        this.addParticipantCtrl = addParticipant.getKey();
        this.addParticipant = new Scene(addParticipant.getValue());

        this.adminOverviewCtrl = adminOverview.getKey();
        this.adminOverview = new Scene(adminOverview.getValue());

        this.loginCtrl = loginPage.getKey();
        this.loginPage = new Scene(loginPage.getValue());

        this.debtOverviewCtrl = debtOverview.getKey();
        this.debtOverview = new Scene(debtOverview.getValue());

        this.settleDebtCtrl = settleDebt.getKey();
        this.settleDebt = new Scene(settleDebt.getValue());

        this.eventStatisticsCtrl = eventStatistics.getKey();
        this.eventStatistics = new Scene(eventStatistics.getValue());

        this.recentEvents = new ArrayList<>();
        showStartScreen();
        primaryStage.show();
    }

    /**
     * Connects the scene controllers to the server via WebSocket for all user scenes,
     * and via Long Polling for the admin scene.
     */
    public void connectToServer() {
        manageExpenseCtrl.connectWebSocket();
        editTitleCtrl.connectWebSocket();
        editExpenseTypeCtrl.connectWebSocket();
        eventOverviewCtrl.connectWebSocket();
        startScreenCtrl.connectWebSocket();
        invitationCtrl.connectWebSocket();
        addParticipantCtrl.connectWebSocket();
        adminOverviewCtrl.connectLongPolling();
        debtOverviewCtrl.connectWebSocket();
        settleDebtCtrl.connectWebSocket();
        eventStatisticsCtrl.connectWebSocket();
    }

    /**
     * Displays the manage expense scene in the primary stage.
     * Also sets up key event handling for the manage expense scene
     *
     * @param event   the event for which we are going to add/edit expenses
     * @param expense the expense whose scene we want to show
     */
    public void showManageExpense(Event event, Expense expense) {
        manageExpenseCtrl.setEvent(event);
        manageExpenseCtrl.setExpense(expense);
        manageExpenseCtrl.clearFields();
        manageExpenseCtrl.refresh(event, expense);
        primaryStage.setScene(manageExpense);
        manageExpense.setOnKeyPressed(e -> manageExpenseCtrl.keyPressed(e));
    }

    /**
     * Displays the event overview scene.
     *
     * @param e event
     */
    public void showEventOverview(Event e) {
        eventOverviewCtrl.setEvent(e);
        eventOverviewCtrl.refresh();
        primaryStage.setScene(eventOverview);
        updateRecentEvents(e);
        eventOverview.setOnKeyPressed(e2 -> eventOverviewCtrl.keyPressed(e2));
    }

    /**
     * Updates the recent events list with the given event.
     *
     * @param e the event to be added to the recent events list
     */
    public void updateRecentEvents(Event e) {
        int size = Math.max(0, recentEvents.size() - 4);
        if (!recentEvents.subList(size, recentEvents.size()).contains(e)) {
            int index = recentEvents.stream().map(Event::getInviteCode)
                    .toList().indexOf(e.getInviteCode());
            if (index == -1) {
                recentEvents.add(e);
            } else {
                recentEvents.set(index, e);
            }
        }
    }

    /**
     * Removes the given event from the recent events list.
     *
     * @param e the event to be removed from the recent events list
     */
    public void removeFromRecentEvents(Event e) {
        recentEvents.remove(e);
        startScreenCtrl.refresh();
    }

    /**
     * Displays the start screen scene
     */
    public void showStartScreen() {
        startScreenCtrl.clearFields();
        startScreenCtrl.refresh();
        primaryStage.setScene(startScreen);
    }

    /**
     * Displays the StartScreen scene
     *
     * @param e invite code of the event
     */
    public void showInvitation(Event e) {
        boolean isUUid = Participant.uuidValidator(System.getProperty("from.email"));
        boolean isNotEmail = !InvitationCtrl.emailValid(System.getProperty("from.email"));
        invitationCtrl.getSendInvite().setDisable(isUUid || isNotEmail);
        invitationCtrl.setEvent(e);
        invitationCtrl.refresh();
        primaryStage.setScene(invitation);
        invitation.setOnKeyPressed(e2 -> invitationCtrl.keyPressed(e2));
    }

    /**
     * Displays the add participant scene
     *
     * @param event the Event
     */
    public void showAddParticipant(Event event) {
        addParticipantCtrl.setEvent(event);
        addParticipantCtrl.refresh();
        primaryStage.setScene(addParticipant);
        addParticipant.setOnKeyPressed(e -> {
            try {
                addParticipantCtrl.keyPressed(e);
            } catch (CloneNotSupportedException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * shows the "Are you sure ?" scene for participant
     */
    public void showDeleteScene() {
        primaryStage.setScene(acceptDelete);
    }

    /**
     * Displays the admin overview scene.
     */
    public void showAdminOverview() {
        adminOverviewCtrl.connectLongPolling();
        adminOverviewCtrl.refresh();
        primaryStage.setScene(adminOverview);
    }

    /**
     * Displays login scene
     */
    public void showLogin() {
        loginCtrl.clear();
        primaryStage.setScene(loginPage);
    }

    /**
     * Displays the debt overview.
     *
     * @param event the event the debt overview is associated with.
     */
    public void showDebtOverview(Event event) {
        debtOverviewCtrl.setEvent(event);
        debtOverviewCtrl.refresh();
        primaryStage.setScene(debtOverview);
    }

    /**
     * Shows the statistics for the expenses associated with this event
     *
     * @param event the event whose statistics will be displayed.
     */
    public void showEventStatistics(Event event) {
        eventStatisticsCtrl.setEvent(event);
        eventStatisticsCtrl.refresh();
        primaryStage.setScene(eventStatistics);
    }

    /**
     * Displays the settle debt scene
     *
     * @param event the event the debt is associated with
     * @param debt  the debt to be settled
     */
    public void showSettleDebt(Event event, Debt debt) {
        settleDebtCtrl.setEvent(event);
        settleDebtCtrl.setDebt(debt);
        secondaryStage.setScene(settleDebt);
        secondaryStage.show();

    }

    /**
     * @return returns the recent Events sublist to be displayed
     */
    public List<Event> recentEventsSublist() {
        int size = Math.max(0, recentEvents.size() - 4);
        return recentEvents.subList(size, recentEvents.size());
    }

    /**
     * Displays the edit title scene
     *
     * @param e - Event on which we are operating
     */
    public void showEditTitle(Event e) {
        editTitleCtrl.setEvent(e);
        primaryStage.setScene(editTitle);
        editTitle.setOnKeyPressed(e2 -> editTitleCtrl.keyPressed(e2));
    }

    /**
     * Displays the edit expense type scene
     *
     * @param event   Event on which we are operating
     * @param expense Expense on which we are operating
     * @param type    an expense type represented as a pair of text and color
     */
    public void showEditExpenseType(Event event, Expense expense, Map.Entry<String, String> type) {
        editExpenseTypeCtrl.setEvent(event);
        editExpenseTypeCtrl.setExpense(expense);
        editExpenseTypeCtrl.setExpenseType(type);
        primaryStage.setScene(editExpenseType);
        editExpenseType.setOnKeyPressed(e -> editExpenseTypeCtrl.keyPressed(e));
    }

    /**
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Gets the recent events list.
     *
     * @return the recent events list
     */
    public List<Event> getRecentEvents() {
        return recentEvents;
    }

    /**
     * Updates the language of the application.
     *
     * @param locale the locale to update the language to
     */
    public void updateLanguage(Locale locale) {
        startScreenCtrl.setLanguage(locale);
        eventOverviewCtrl.setLanguage(locale);

        Properties prop = new Properties();
        String configPath = System.getProperty("configPath");
        try (InputStream input = new FileInputStream(configPath)) {
            prop.load(input);
        } catch (IOException ignored) {
        }

        try (Writer writer = new FileWriter(configPath)) {
            prop.setProperty("language", locale.getLanguage());
            prop.store(writer, null);
        } catch (IOException ignored) {
        }
    }
}