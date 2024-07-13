package client.scenes;

import client.utils.Language;
import client.utils.LoginUtils;
import client.utils.ServerUtils;
import commons.AdminUser;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;


public class LoginCtrl {

    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logIn;
    @FXML
    private Button back;
    @FXML
    private Button generatePassword;
    @FXML
    private Button clear;
    @FXML
    private Label enterYourAdminPassword;
    @FXML
    private Label label;

    private MainCtrl mainCtrl;
    private ServerUtils server;
    private LoginUtils login;

    /**
     * Constructor for controller
     *
     * @param mainCtrl to redirect
     * @param server   server utils to connect to server
     * @param login    login utils to generate password
     */
    @Inject
    public LoginCtrl(MainCtrl mainCtrl, ServerUtils server, LoginUtils login) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.login = login;
    }

    /**
     * Initializes the language
     */
    @FXML
    public void initialize() {
        label.textProperty().bind(Language.createStringBinding("loginPageWelcome"));
        enterYourAdminPassword.textProperty().bind(Language.createStringBinding
                ("loginPageEnterYourAdminPassword"));
        generatePassword.textProperty().bind(Language.createStringBinding
                ("loginPageGeneratePassword"));
        passwordField.promptTextProperty().bind(Language.createStringBinding
                ("loginPagePasswordPrompt"));
        logIn.textProperty().bind(Language.createStringBinding("loginPageLogIn"));
        back.textProperty().bind(Language.createStringBinding("loginPageBack"));
        clear.textProperty().bind(Language.createStringBinding("loginPageClear"));
    }

    /**
     * Directs to admin overview if the credentials are correct
     */
    public void toAdminOverview() {
        try {
            boolean valid = server.getAdminUser(passwordField.getText()) != null;
            passwordField.setStyle("-fx-border-color: grey ; -fx-border-width: 0.5px ;");
            mainCtrl.showAdminOverview();
        } catch (Exception e) {
            passwordField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.headerTextProperty().bind(Language.createStringBinding("incorrectPassword"));
            errorAlert.contentTextProperty().bind(Language
                    .createStringBinding("loginPleaseTryAgain"));
            errorAlert.show();
            passwordField.clear();
        }
    }

    /**
     * Generates a password to use in order to log in
     */
    public void passGen() {

        AdminUser user = new AdminUser(login.randPass());

        try {
            server.addUser(user);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Directs to start screen
     */
    public void toStartScreen() {
        mainCtrl.showStartScreen();
    }

    /**
     * Clears the password field
     */
    public void clear() {
        passwordField.clear();
    }

    /**
     * Getter for password field
     *
     * @return password field
     */
    public TextField getPasswordField() {
        return passwordField;
    }
}
