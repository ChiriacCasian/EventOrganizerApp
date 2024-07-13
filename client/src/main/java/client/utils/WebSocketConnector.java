package client.utils;

import client.scenes.MainCtrl;
import com.google.inject.Inject;
import commons.Event;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

public abstract class WebSocketConnector {

    protected ServerUtils server;

    protected MainCtrl mainCtrl;

    protected Event event;

    /**
     * Constructor for the WebSocketConnector
     *
     * @param server   the server instance used to connect to the websocket
     * @param mainCtrl the main controller instance used to refresh the view
     */
    @Inject
    public WebSocketConnector(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Connects to the server via websocket and
     * listens for updates to or deletion of the event
     */
    public void connectWebSocket() {
        server.registerForUpdatesWS("/topic/events/update", Event.class, e -> {
            if (e.getInviteCode().equals(event.getInviteCode())) {
                event = e;
                event.updateFields();
                Platform.runLater(this::refresh);
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
                        errorAlert.showAndWait();
                        mainCtrl.showStartScreen();
                    });
                }
            }
        });
        server.registerForUpdatesWS("/topic/events/import", Event.class, e -> {
            if (e.getInviteCode().equals(event.getInviteCode())) {
                event = e;
                event.updateFields();
                Platform.runLater(this::refresh);
            }
        });
    }


    /**
     * Refreshes the scene
     */
    public abstract void refresh();

    /**
     * Getter for the scene
     *
     * @return the scene
     */
    public abstract Scene getScene();

    /**
     * Getter for the event
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Setter for the event
     *
     * @param event the event
     */
    public void setEvent(Event event) {
        this.event = event;
        refresh();
    }
}
