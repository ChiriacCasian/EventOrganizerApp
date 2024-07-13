package client.utils;

import javafx.scene.control.Alert;

public class ErrorConnectionAlert {
    private static Alert instance;

    private ErrorConnectionAlert() {
    }

    /**
     * Returns the instance of the Alert
     *
     * @return the instance of the Alert
     */
    public static Alert getInstance() {
        if (instance == null) {
            instance = new Alert(Alert.AlertType.ERROR);
            instance.setTitle("Splitty");
            instance.headerTextProperty().bind(Language.createStringBinding("error"));
            instance.contentTextProperty().bind(Language.createStringBinding("connectionClosed"));
        }
        return instance;
    }

    /**
     * Shows the alert
     */
    public static void showAlert() {
        if (instance == null || !instance.isShowing()) {
            getInstance().showAndWait();
        }
    }
}