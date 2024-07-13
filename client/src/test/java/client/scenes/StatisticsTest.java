package client.scenes;

import client.MyFXML;
import client.MyModule;
import client.utils.Language;
import client.utils.ServerUtils;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commons.Event;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StatisticsTest extends ApplicationTest {
    @Mock
    private ServerUtils server;
    @Mock
    private MainCtrl mainCtrl;

    @InjectMocks
    private EventStatisticsCtrl controller;

    private Scene sceneVerification;

    private Event event;

    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "0");
    }

    @AfterEach
    public void resetMocks() {
        Mockito.reset(server, mainCtrl);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("configPath", "src/main/resources/config.properties");
        Injector injector = Guice.createInjector(new MyModule());
        MyFXML FXML = new MyFXML(injector);
        Pair<EventStatisticsCtrl, Parent> statistics = FXML.load(EventStatisticsCtrl.class,
                "client", "scenes", "EventStatistics.fxml");
        this.controller = statistics.getKey();
        MockitoAnnotations.openMocks(this).close();
        Scene scene = new Scene(statistics.getValue());
        sceneVerification = scene;
        stage.setScene(scene);
        stage.show();
        Language.setLanguage(Locale.ENGLISH);
    }

    @Test
    public void refreshTest() {
        this.controller = Mockito.spy(this.controller);
        event = new Event();
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(this.controller).refresh();
        Mockito.verify(this.controller).displayPieChart();
    }

    @Test
    public void getSceneTest() {
        assert (this.controller.getScene() == sceneVerification);
    }

    @Test
    public void displayPieChartTest() {
        this.controller = Mockito.spy(this.controller);
        event = Mockito.spy(new Event());
        event.setCurrency("EUR");
        event.setTitle("Test");
        Mockito.when(event.totalExpenses()).thenReturn(100.0);
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });

        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(this.controller).displayPieChart();
        assertEquals("Division of pricer per tags for the expenses of event Test", (
                (PieChart) this.controller.getGroup().getChildren().getFirst()).getTitle());
        assertEquals("Total sum of expenses: 100.0 EUR", this.controller.getTotalAmount().getText());
    }

    @Test
    public void displayPieChartDutchTest() {
        Platform.runLater(() -> {
            Language.setLanguage(Locale.forLanguageTag("nl"));
        });
        WaitForAsyncUtils.waitForFxEvents();
        this.controller = Mockito.spy(this.controller);
        event = Mockito.spy(new Event());
        event.setCurrency("EUR");
        event.setTitle("Test");
        Mockito.when(event.totalExpenses()).thenReturn(100.0);
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });

        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(this.controller).displayPieChart();
        assertEquals("Verdeling van prijzen per tags voor de uitgaven van evenement Test", (
                (PieChart) this.controller.getGroup().getChildren().getFirst()).getTitle());
        assertEquals("Totale som van uitgaven: 100.0 EUR", this.controller.getTotalAmount().getText());
    }

    @Test
    public void displayPieChartGermanTest() {
        Platform.runLater(() -> {
            Language.setLanguage(Locale.forLanguageTag("de"));
        });
        WaitForAsyncUtils.waitForFxEvents();
        this.controller = Mockito.spy(this.controller);
        event = Mockito.spy(new Event());
        event.setCurrency("EUR");
        event.setTitle("Test");
        Mockito.when(event.totalExpenses()).thenReturn(100.0);
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });

        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(this.controller).displayPieChart();
        assertEquals("Aufteilung der Preise pro Typ f\u00fcr die Ausgaben des Events Test", (
                (PieChart) this.controller.getGroup().getChildren().getFirst()).getTitle());
        assertEquals("Gesamtsumme der Ausgaben: 100.0 EUR", this.controller.getTotalAmount().getText());
    }

    @Test
    public void displayPieChartBulgarianTest() {
        Platform.runLater(() -> {
            Language.setLanguage(Locale.forLanguageTag("bg"));
        });
        WaitForAsyncUtils.waitForFxEvents();
        this.controller = Mockito.spy(this.controller);
        event = Mockito.spy(new Event());
        event.setCurrency("EUR");
        event.setTitle("Test");
        Mockito.when(event.totalExpenses()).thenReturn(100.0);
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });

        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(this.controller).displayPieChart();
        assertEquals("\u0420\u0430\u0437\u0434\u0435\u043b\u0435" +
                            "\u043d\u0438\u0435\u0020\u043d\u0430\u0020\u0446\u0435" +
                            "\u043d\u0438\u0442\u0435\u0020\u043f\u043e\u0020\u0442\u0430" +
                            "\u0433\u043e\u0432\u0435\u0020\u0437\u0430\u0020\u0440\u0430" +
                            "\u0437\u0445\u043e\u0434\u0438\u0442\u0435\u0020\u043d\u0430" +
                            "\u0020\u0441\u044a\u0431\u0438\u0442\u0438\u0435 Test", (
                (PieChart) this.controller.getGroup().getChildren().getFirst()).getTitle());
        assertEquals("\u041e\u0431\u0449\u0430\u0020\u0441\u0443" +
                        "\u043c\u0430\u0020\u043d\u0430\u0020\u0440\u0430\u0437" +
                        "\u0445\u043e\u0434\u0438\u0442\u0435: 100.0 EUR", this.controller.getTotalAmount().getText());
    }

    @Test
    public void backTest() {
        Mockito.doNothing().when(mainCtrl).showEventOverview(event);
        event = new Event();
        Platform.runLater(() -> {
            this.controller.setEvent(this.event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#back");
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mainCtrl).showEventOverview(event);
    }
}
