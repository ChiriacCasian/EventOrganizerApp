package client.scenes;

import client.utils.Language;
import client.utils.ServerUtils;
import client.utils.WebSocketConnector;
import commons.Event;
import commons.Expense;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStatisticsCtrl extends WebSocketConnector {

    @FXML
    private Label totalAmount;
    @FXML
    private Button back;
    @FXML
    private Group group;
    @FXML
    private Label title;

    /**
     * Constructor for the WebSocketConnector
     *
     * @param server   the server instance used to connect to the websocket
     * @param mainCtrl the main controller instance used to refresh the view
     */
    @Inject
    public EventStatisticsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the language bindings
     */
    @FXML
    public void initialize() {
        title.textProperty().bind(Language.createStringBinding("statistics"));
        back.textProperty().bind(Language.createStringBinding("eventOverviewBack"));
    }

    /**
     * Refresh the page - show the total amount for all expenses for this event
     */
    @Override
    public void refresh() {
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


        totalAmount.setText(totalSumString + ": " +
                (double) Math.round(100 * event.totalExpenses()) / 100 + " " + event.getCurrency());
        displayPieChart();
    }

    /**
     * Closes this scene and returns to the previous one
     */
    public void cancel() {
        mainCtrl.showEventOverview(event);
        group.getChildren().clear();
    }

    /**
     * Returns the scene of the controller
     *
     * @return the scene of the controller
     */
    @Override
    public Scene getScene() {
        return title.getScene();
    }

    /**
     * Sets the event of this controller instance
     *
     * @param event the event
     */
    public void setEvent(Event event) {
        this.event = event;
        refresh();
    }

    /**
     * Displays a pie chart for the expenses of this event
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    public void displayPieChart() {
        if (event != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            List<String> expenseTypes = new ArrayList<>();
            List<Double> expensePrices = new ArrayList<>();
            Map<String, String> expenseTypesColors = new HashMap<>();
            for (Expense expense : event.getExpenses()) {
                for (String type : expense.getExpenseTypes().keySet()) {
                    int index = expenseTypes.indexOf(type);
                    if (index == -1) {
                        expenseTypes.add(type);
                        expensePrices.add(expense.getAmount());
                        expenseTypesColors.put(type, expense.getExpenseTypes().get(type));
                    } else {
                        expensePrices.set(index, expensePrices.get(index) + expense.getAmount());
                    }
                }
            }

            double totalPrice = 0;
            for (int i = 0; i < expenseTypes.size(); i++) {
                pieChartData.add(new PieChart.Data(expenseTypes.get(i), expensePrices.get(i)));
                totalPrice += expensePrices.get(i);
            }

            double finalTotalPrice = totalPrice;
            pieChartData.forEach(data -> {
                data.nameProperty().bind(Bindings.concat(
                        data.getName(), " ", data.getPieValue(), " ", event.getCurrency(), " (",
                        String.format("%.2f", data.getPieValue() / finalTotalPrice * 100), "%)"
                ));
            });
            PieChart chart = new PieChart(pieChartData);

            String chartTitle = "";

            switch (Language.getLanguage().getLanguage()) {
                case "en": {
                    chartTitle = "Division of pricer per tags for the expenses of event ";
                    break;
                }
                case "nl": {
                    chartTitle = "Verdeling van prijzen per tags voor de uitgaven van evenement ";
                    break;
                }
                case "bg": {
                    chartTitle = "\u0420\u0430\u0437\u0434\u0435\u043b\u0435" +
                            "\u043d\u0438\u0435\u0020\u043d\u0430\u0020\u0446\u0435" +
                            "\u043d\u0438\u0442\u0435\u0020\u043f\u043e\u0020\u0442\u0430" +
                            "\u0433\u043e\u0432\u0435\u0020\u0437\u0430\u0020\u0440\u0430" +
                            "\u0437\u0445\u043e\u0434\u0438\u0442\u0435\u0020\u043d\u0430" +
                            "\u0020\u0441\u044a\u0431\u0438\u0442\u0438\u0435 ";
                    break;
                }
                case "de": {
                    chartTitle = "Aufteilung der Preise pro Typ f\u00fcr die Ausgaben des Events ";
                    break;
                }
            }

            chart.setTitle(chartTitle
                    + event.getTitle());
            chart.setClockwise(true);
            chart.setLabelLineLength(40);
            chart.setLabelsVisible(true);
            chart.setStartAngle(0);
            chart.setLegendVisible(false);
            group.getChildren().add(chart);
            for (int i = 0; i < pieChartData.size(); i++) {
                PieChart.Data data = pieChartData.get(i);
                data.getNode().setStyle("-fx-pie-color: " +
                        expenseTypesColors.get(expenseTypes.get(i)) + ";");
            }
        }
    }

    /**
     * Returns the total amount label
     *
     * @return the total amount label
     */
    public Label getTotalAmount() {
        return totalAmount;
    }

    /**
     * Returns the back button
     *
     * @return the back button
     */
    public Button getBack() {
        return back;
    }

    /**
     * Returns the group
     *
     * @return the group
     */
    public Group getGroup() {
        return group;
    }
}
