package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Participant;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;
import java.time.LocalDateTime;
import java.util.Scanner;


public class InvitationCtrl extends WebSocketConnector {

    @FXML
    private Label currentEvent;

    @FXML
    private Label codeLabel;

    @FXML
    private TextArea emails;

    @FXML
    private Button sendInvite;

    @FXML
    private Button back;

    @FXML
    private Label title;

    @FXML
    private Label firstLine;

    @FXML
    private Label secondLine;

    /**
     * Constructs an instance of StartScreenCtrl with the specified dependencies
     *
     * @param server   ServerUtils instance used for server used to communicate with the server
     * @param mainCtrl the mainCtrl for main application flow
     */
    @Inject
    public InvitationCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the WebSocket connection.
     */
    @FXML
    public void initialize() {
        title.textProperty().bind(Language.createStringBinding("invitationTitle"));
        firstLine.textProperty().bind(Language.createStringBinding("invitationFirstLine"));
        secondLine.textProperty().bind(Language.createStringBinding("invitationSecondLine"));
        back.textProperty().bind(Language.createStringBinding("invitationBack"));
        sendInvite.textProperty().bind(Language.createStringBinding("invitationSendInvites"));
    }

    /**
     * Returns the send invite button
     *
     * @return the send invite button
     */
    public Button getSendInvite() {
        return sendInvite;
    }


    /**
     * Sends invite to given email addresses
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    public void sendInvite() {
        String emailString = emails.getText();
        if (emailString.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("The text field is empty");
            errorAlert.setContentText("Please Enter an email address!");
            errorAlert.showAndWait();
        }
        boolean sent = false;
        Scanner eScanner = new Scanner(emailString);
        eScanner.useDelimiter("\n");
        String sub = "You have been invited to a Splitty Event!";
        String bod = "The invite code of the event is: " + event.getInviteCode() +
                "\nThe server URL is: " + System.getProperty("server.host");
        while (eScanner.hasNext()) {
            String line = eScanner.nextLine();
            int spaceIndex = line.indexOf(" ");
            String mail;
            String name = null;
            if (spaceIndex == -1) {
                mail = line;
            } else {
                mail = line.substring(0, spaceIndex);
                name = line.substring(spaceIndex + 1);
            }
            if (!emailValid(mail)) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Invalid Email address: " + mail);
                errorAlert.setContentText("Please Enter a valid email address!");
                errorAlert.showAndWait();
            } else {
                try {
                    server.sendMail(mail, sub, bod);
                    if (name != null) {
                        addParticipant(name, mail);
                    }
                    sent = true;
                } catch (WebApplicationException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Error: " + mail);
                    errorAlert.setContentText("Something went wrong");
                    errorAlert.showAndWait();
                }
            }
        }
        if (sent) {
            Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmAlert.setHeaderText("Action completed!");
            confirmAlert.setContentText("Invitation Email has been sent!!!");
            confirmAlert.showAndWait();
        }
    }


    private void addParticipant(String name, String email) {
        Participant add = new Participant(name, email, event);
        event.addParticipant(add);
        event.setDateOfModification(LocalDateTime.now());
        server.send("/app/events/update", event);
    }

    /**
     * Copies to clipboard when clicking label
     */
    public void clipCopy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(codeLabel.getText());
        clipboard.setContent(content);
    }

    /**
     * Refreshes the invitation scene
     */
    public void refresh() {
        currentEvent.setText(event.getTitle());
        codeLabel.setText(event.getInviteCode());
    }


    /**
     * Handles key events, such as pressing Enter or Escape keys.
     * Calls the corresponding methods (ok() or cancel()) based on the pressed key.
     *
     * @param e the KeyEvent object representing the key event
     */
    public void keyPressed(KeyEvent e) {
        if (Objects.requireNonNull(e.getCode()) == KeyCode.ESCAPE) {
            goBack();
        }
    }

    /**
     * Goes back to the Event Overview
     */
    public void goBack() {
        emails.clear();
        mainCtrl.showEventOverview(event);
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
     * @param email email to check
     * @return checks whether email valid
     */
    public static boolean emailValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[1].isEmpty() || parts[0].isEmpty()) {
            return false;
        }
        String[] domainParts = parts[1].split("\\.");
        if (domainParts.length <= 1) {
            return false;
        }
        for (String part : domainParts) {
            if (part.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
