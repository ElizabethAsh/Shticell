package components.main;

import components.login.LoginController;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import components.dashboardComponents.dashboardRoom.DashboardController;
import impl.engine.SheetDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import util.alert.AlertUtils;
import java.io.IOException;
import java.net.URL;
import static util.constants.Constants.*;

public class SheetAppMainController {
    private ScrollPane loginComponent;
    private LoginController loginController;

    private BorderPane sheetRoomComponent;
    private SheetRoomMainController sheetRoomComponentController;

    private BorderPane dashboardComponent;
    private DashboardController dashboardComponentController;

    @FXML private AnchorPane mainPanel;

    private final StringProperty currentUserName;

    public SheetAppMainController() {
        currentUserName = new SimpleStringProperty(ANONYMOUS);
    }

    @FXML
    public void initialize() {
        loadLoginPage();
        loadSheetRoomPage();
        loadDashboardPage();
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }

    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loginController = fxmlLoader.getController();
            loginController.setSheetAppMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            AlertUtils.showErrorMessage("Failed to load login page: " + e.getMessage());
        }
    }

    private void loadDashboardPage() {
        URL loginPageUrl = getClass().getResource(DASH_BOARD_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            dashboardComponent = fxmlLoader.load();
            dashboardComponentController = fxmlLoader.getController();
            dashboardComponentController.setSheetAppMainController(this);
            dashboardComponentController.setTopBarControllerUserName(currentUserName);
        } catch (IOException e) {
            AlertUtils.showErrorMessage("Failed to load login page: " + e.getMessage());
        }
    }

    private void loadSheetRoomPage() {
        URL SheetPageUrl = getClass().getResource(SHEET_ROOM_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(SheetPageUrl);
            sheetRoomComponent = fxmlLoader.load();
            sheetRoomComponentController = fxmlLoader.getController();
            sheetRoomComponentController.setSheetAppMainController(this);
        } catch (IOException e) {
            AlertUtils.showErrorMessage("Failed to load login page: " + e.getMessage());
        }
    }

    public void switchToDashboardRoom() {
        if (sheetRoomComponentController != null) {
            sheetRoomComponentController.close();
        }

        setMainPanelTo(dashboardComponent);
        dashboardComponentController.setActive();
    }

    public void switchToSheetRoom(){
        if (dashboardComponentController != null) {
            dashboardComponentController.close();
        }

        setMainPanelTo(sheetRoomComponent);
        sheetRoomComponentController.setActive();
    }

    public void loadSheet(SheetDTO sheetDTO) {
        sheetRoomComponentController.loadSheet(sheetDTO);
        switchToSheetRoom();
    }

    public String getCurrentUserName(){
        return currentUserName.getValue();
    }
}
