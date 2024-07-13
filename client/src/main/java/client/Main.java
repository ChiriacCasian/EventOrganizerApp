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

package client;

import client.scenes.*;
import com.google.inject.Injector;
import jakarta.ws.rs.client.ClientBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import org.glassfish.jersey.client.ClientConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.inject.Guice.createInjector;
import static org.springframework.http.HttpHeaders.SERVER;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    private static MainCtrl mainCtrl;

    private static String email;

    private static String password;

    /**
     * Main function for the client controllers
     *
     * @param args arguments
     * @throws URISyntaxException if the URI is not valid syntax
     * @throws IOException        if it is not a valid input
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("configPath")));
        String serverHost = properties.getProperty("server.host");
        String email = properties.getProperty("from.email");
        String password = properties.getProperty("from.password");
        System.setProperty("server.host", serverHost);
        System.setProperty("from.email", email);
        System.setProperty("from.password", password);
        launch();
        ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/admin/deleteAll");
    }


    /**
     * Starts the application loading FXML files and add quote views.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        var manageExpense = FXML.load(ManageExpenseCtrl.class, "client", "scenes",
                "ManageExpense.fxml");
        var eventOverview = FXML.load(EventOverviewCtrl.class, "client", "scenes",
                "EventOverview.fxml");
        var invitation = FXML.load(InvitationCtrl.class, "client", "scenes", "Invitation.fxml");
        var addParticipant = FXML.load(AddParticipantCtrl.class, "client", "scenes",
                "AddParticipant.fxml");
        var startScreen = FXML.load(StartScreenCtrl.class, "client", "scenes", "StartScreen.fxml");
        var adminOverview = FXML.load(AdminOverviewCtrl.class, "client", "scenes",
                "AdminOverview.fxml");
        var loginScene = FXML.load(LoginCtrl.class, "client", "scenes",
                "LoginPage.fxml");
        var settleDebt = FXML.load(SettleDebtCtrl.class, "client", "scenes", "SettleDebt.fxml");
        var editTitle = FXML.load(EditTitleCtrl.class, "client", "scenes", "EditTitle.fxml");
        var editExpenseType = FXML.load(EditExpenseTypeCtrl.class,
                "client", "scenes", "EditExpenseType.fxml");
        var debtOverview = FXML.load(DebtOverviewCtrl.class, "client", "scenes",
                "DebtOverview.fxml");
        var acceptDelete =
                FXML.load(AddParticipantCtrl.class, "client", "scenes", "AcceptDelete.fxml");
        var eventStatistics = FXML.load(EventStatisticsCtrl.class,
                "client", "scenes", "EventStatistics.fxml");
        mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        primaryStage.setOnCloseRequest(e -> adminOverview.getKey().stop());

        mainCtrl.initialize(primaryStage, manageExpense, eventOverview,
                startScreen, invitation, addParticipant, adminOverview, loginScene,
                editTitle, settleDebt, editExpenseType,
                acceptDelete, debtOverview, eventStatistics);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable connectTask = new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                } catch (Exception e) {
                    executorService.schedule(this, 10, TimeUnit.SECONDS);
                }
            }
        };
        executorService.submit(connectTask);
    }

    /**
     * Connects to the server.
     */
    public static void connect() {
        mainCtrl.connectToServer();
    }

    /**
     * getter for email
     *
     * @return returns the email
     */
    public static String getEmail() {
        return email;
    }

    /**
     * getter for password
     *
     * @return returns the password
     */
    public static String getPassword() {
        return password;
    }
}