package client.utils;

import client.scenes.*;
import commons.Debt;
import commons.Event;
import commons.Expense;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MainCtrlTest {
    @InjectMocks
    MainCtrl mainCtrl;
    @Mock
    Stage primaryStage;
    @Mock
    Pair<ManageExpenseCtrl, Parent> manageExpenseScene;
    @Mock
    Pair<EventOverviewCtrl, Parent> eventOverview;
    @Mock
    Pair<StartScreenCtrl, Parent> startScreen;
    @Mock
    Pair<InvitationCtrl, Parent> invitation;
    @Mock
    Pair<AddParticipantCtrl, Parent> addParticipant;
    @Mock
    Pair<AdminOverviewCtrl, Parent> adminOverview;
    @Mock
    Pair<LoginCtrl, Parent> loginPage;
    @Mock
    Pair<EditTitleCtrl, Parent> editTitle;
    @Mock
    Pair<SettleDebtCtrl, Parent> settleDebt;
    @Mock
    Pair<EditExpenseTypeCtrl, Parent> editExpenseType;
    @Mock
    Pair<AddParticipantCtrl, Parent> acceptDelete;
    @Mock
    Pair<DebtOverviewCtrl, Parent> debtOverview;
    @Mock
    Pair<EventStatisticsCtrl, Parent> eventStatistics;
    @Mock
    ManageExpenseCtrl manageExpenseCtrl;
    @Mock
    EventOverviewCtrl eventOverviewCtrl;
    @Mock
    StartScreenCtrl startScreenCtrl;
    @Mock
    InvitationCtrl invitationCtrl;
    @Mock
    AddParticipantCtrl addParticipantCtrl;
    @Mock
    AdminOverviewCtrl adminOverviewCtrl;
    @Mock
    LoginCtrl loginCtrl;
    @Mock
    EditTitleCtrl editTitleCtrl;
    @Mock
    SettleDebtCtrl settleDebtCtrl;
    @Mock
    EditExpenseTypeCtrl editExpenseTypeCtrl;
    @Mock
    AddParticipantCtrl acceptDeleteCtrl;
    @Mock
    DebtOverviewCtrl debtOverviewCtrl;
    @Mock
    EventStatisticsCtrl eventStatisticsCtrl;

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("from.email", "splittyoopp@gmail.com");
        System.setProperty("from.password", "dqthehoktabtjdmq");
        System.setProperty("configPath", "src/main/resources/config.properties");
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.startup(() -> {
                });
            }
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    public void setUp() {
        Mockito.when(manageExpenseScene.getKey()).thenReturn(manageExpenseCtrl);
        Mockito.when(eventOverview.getKey()).thenReturn(eventOverviewCtrl);
        Mockito.when(startScreen.getKey()).thenReturn(startScreenCtrl);
        Mockito.when(invitation.getKey()).thenReturn(invitationCtrl);
        Mockito.when(addParticipant.getKey()).thenReturn(addParticipantCtrl);
        Mockito.when(adminOverview.getKey()).thenReturn(adminOverviewCtrl);
        Mockito.when(loginPage.getKey()).thenReturn(loginCtrl);
        Mockito.when(editTitle.getKey()).thenReturn(editTitleCtrl);
        Mockito.when(settleDebt.getKey()).thenReturn(settleDebtCtrl);
        Mockito.when(editExpenseType.getKey()).thenReturn(editExpenseTypeCtrl);
        Mockito.when(debtOverview.getKey()).thenReturn(debtOverviewCtrl);
        Mockito.when(eventStatistics.getKey()).thenReturn(eventStatisticsCtrl);

        Mockito.when(manageExpenseScene.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(eventOverview.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(startScreen.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(invitation.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(addParticipant.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(adminOverview.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(loginPage.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(editTitle.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(settleDebt.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(editExpenseType.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(acceptDelete.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(debtOverview.getValue()).thenReturn(new Parent() {
        });
        Mockito.when(eventStatistics.getValue()).thenReturn(new Parent() {
        });

        Mockito.doNothing().when(eventOverviewCtrl).setFlags();
        Mockito.doNothing().when(eventOverviewCtrl).setGraphics();
        Mockito.doNothing().when(startScreenCtrl).setFlags();

        Platform.runLater(() -> mainCtrl.initialize(primaryStage, manageExpenseScene, eventOverview, startScreen, invitation, addParticipant,
                adminOverview, loginPage, editTitle, settleDebt, editExpenseType, acceptDelete, debtOverview, eventStatistics));

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void initializeTest() {
        Mockito.verify(primaryStage).show();
        Mockito.verify(startScreenCtrl).clearFields();
        Mockito.verify(startScreenCtrl).refresh();
        Mockito.verify(primaryStage).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void connectToServerTest() {
        Platform.runLater(() -> mainCtrl.connectToServer());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(manageExpenseCtrl).connectWebSocket();
        Mockito.verify(editTitleCtrl).connectWebSocket();
        Mockito.verify(editExpenseTypeCtrl).connectWebSocket();
        Mockito.verify(eventOverviewCtrl).connectWebSocket();
        Mockito.verify(startScreenCtrl).connectWebSocket();
        Mockito.verify(invitationCtrl).connectWebSocket();
        Mockito.verify(addParticipantCtrl).connectWebSocket();
        Mockito.verify(adminOverviewCtrl).connectLongPolling();
        Mockito.verify(debtOverviewCtrl).connectWebSocket();
        Mockito.verify(settleDebtCtrl).connectWebSocket();
    }

    @Test
    public void showManageExpenseTest() {
        Platform.runLater(() -> mainCtrl.showManageExpense(Mockito.mock(Event.class),
                Mockito.mock(Expense.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(primaryStage, Mockito.atLeastOnce()).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void updateRecentEventsContainsTest() {
        Event event = Mockito.mock(Event.class);
        mainCtrl.getRecentEvents().add(event);
        Platform.runLater(() -> mainCtrl.updateRecentEvents(event));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(event, Mockito.never()).getInviteCode();
    }

    @Test
    public void updateRecentEventsNotContainsAddTest() {
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn(Event.generateInviteCode());
        Platform.runLater(() -> mainCtrl.updateRecentEvents(event));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(event).getInviteCode();
        assertTrue(mainCtrl.getRecentEvents().contains(event));
    }

    @Test
    public void updateRecentEventsNotContainsUpdateTest() {
        Event eventInList = new Event();
        String inviteCode = Event.generateInviteCode();
        eventInList.setInviteCode(inviteCode);
        mainCtrl.getRecentEvents().add(eventInList);
        Event event = Mockito.mock(Event.class);
        Mockito.when(event.getInviteCode()).thenReturn(inviteCode);
        Platform.runLater(() -> mainCtrl.updateRecentEvents(event));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(event).getInviteCode();
        assertFalse(mainCtrl.getRecentEvents().contains(eventInList));
        assertTrue(mainCtrl.getRecentEvents().contains(event));
    }

    @Test
    public void removeFromRecentEventsTest() {
        Event event = Mockito.mock(Event.class);
        mainCtrl.getRecentEvents().add(event);
        Platform.runLater(() -> mainCtrl.removeFromRecentEvents(event));
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(mainCtrl.getRecentEvents().contains(event));
        Mockito.verify(startScreenCtrl, Mockito.atLeast(2)).refresh();
    }

    @Test
    public void recentEventsSubListTest() {
        Event event = Mockito.mock(Event.class);
        mainCtrl.getRecentEvents().addAll(List.of(
                event, event, event, event, event));
        assertEquals(4, mainCtrl.recentEventsSublist().size());
    }

    @Test
    public void showEventOverview() {
        Platform.runLater(() -> mainCtrl.showEventOverview(Mockito.mock(Event.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showInvitation() {
        Event event = Mockito.mock(Event.class);
        Button button = Mockito.mock(Button.class);
        Mockito.when(invitationCtrl.getSendInvite()).thenReturn(button);
        Platform.runLater(() -> mainCtrl.showInvitation(event));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(invitationCtrl).setEvent(event);
        Mockito.verify(invitationCtrl).refresh();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showAddParticipantTest() {
        Event event = Mockito.mock(Event.class);
        Platform.runLater(() -> mainCtrl.showAddParticipant(event));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(addParticipantCtrl).setEvent(event);
        Mockito.verify(addParticipantCtrl).refresh();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showDeleteTest() {
        Platform.runLater(() -> mainCtrl.showDeleteScene());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showAdminOverviewTest() {
        Platform.runLater(() -> mainCtrl.showAdminOverview());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(adminOverviewCtrl).connectLongPolling();
        Mockito.verify(adminOverviewCtrl).refresh();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showLoginTest() {
        Platform.runLater(() -> mainCtrl.showLogin());
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(loginCtrl).clear();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showDebtOverviewTest() {
        Platform.runLater(() -> mainCtrl.showDebtOverview(Mockito.mock(Event.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(debtOverviewCtrl).setEvent(Mockito.any(Event.class));
        Mockito.verify(debtOverviewCtrl).refresh();
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showSettleDebtTest() {
        Platform.runLater(() -> mainCtrl.showSettleDebt(Mockito.mock(Event.class),
                Mockito.mock(Debt.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(settleDebtCtrl).setEvent(Mockito.any(Event.class));
        Mockito.verify(settleDebtCtrl).setDebt(Mockito.any(Debt.class));
        Mockito.verify(primaryStage, Mockito.atMost(1)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showEditTitleTest() {
        Platform.runLater(() -> mainCtrl.showEditTitle(Mockito.mock(Event.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(editTitleCtrl).setEvent(Mockito.any(Event.class));
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void showEditExpenseTypeTest() {
        Platform.runLater(() -> mainCtrl.showEditExpenseType(Mockito.mock(Event.class),
                Mockito.mock(Expense.class), Mockito.mock(Map.Entry.class)));
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(editExpenseTypeCtrl).setEvent(Mockito.any(Event.class));
        Mockito.verify(editExpenseTypeCtrl).setExpense(Mockito.any(Expense.class));
        Mockito.verify(editExpenseTypeCtrl).setExpenseType(Mockito.any(Map.Entry.class));
        Mockito.verify(primaryStage, Mockito.atLeast(2)).setScene(Mockito.any(Scene.class));
    }

    @Test
    public void getPrimaryStageTest() {
        assertEquals(primaryStage, mainCtrl.getPrimaryStage());
    }

}
