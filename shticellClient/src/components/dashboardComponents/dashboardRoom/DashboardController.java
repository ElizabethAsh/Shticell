package components.dashboardComponents.dashboardRoom;

import components.main.SheetAppMainController;
import components.dashboardComponents.PermissionsTable.PermissionsTableController;
import components.dashboardComponents.commandsPanel.CommandsPanelController;
import components.dashboardComponents.availableSheetsTable.AvailableSheetsTableController;
import components.dashboardComponents.topBar.TopBarController;
import impl.engine.SheetDTO;
import impl.engine.SheetDetailsDTO;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.Closeable;

public class DashboardController implements Closeable {
    @FXML private SplitPane splitPane;

    @FXML private VBox topBarComponent;
    @FXML private TopBarController topBarComponentController;

    @FXML private AnchorPane availableSheetsTableComponent;
    @FXML private AvailableSheetsTableController availableSheetsTableComponentController;

    @FXML private AnchorPane permissionsTableComponent;
    @FXML private PermissionsTableController permissionsTableComponentController;

    @FXML private VBox commandsPanelComponent;
    @FXML private CommandsPanelController commandsPanelComponentController;

    @FXML
    public void initialize() {
        commandsPanelComponentController.setMainController(this);
        availableSheetsTableComponentController.setMainController(this);
        permissionsTableComponent.setVisible(false);
    }

    private SheetAppMainController sheetAppMainController;

    public void setSheetAppMainController(SheetAppMainController sheetAppMainController) {
        this.sheetAppMainController = sheetAppMainController;
    }

    public void setTopBarControllerUserName(StringProperty userNameProperty) {
        topBarComponentController.setUserNameProperty(userNameProperty);
    }

    public void setActive(){
        availableSheetsTableComponentController.startSheetsRefresher();
    }

    public void loadSheet(SheetDTO sheetDTO) {
        sheetAppMainController.loadSheet(sheetDTO);
    }

    public SheetDetailsDTO getSelectedSheetFromOverview() {
        return availableSheetsTableComponentController.getSelectedSheet();
    }

    public void showPermissionsTable(String sheetName) {
        if (!permissionsTableComponent.isVisible()){
            permissionsTableComponent.setVisible(true);
        }

        permissionsTableComponentController.stopPermissionsTableRefresher();
        permissionsTableComponentController.startPermissionsTableRefresher(sheetName);
    }

    public void hidePermissionsTable() {
        permissionsTableComponentController.stopPermissionsTableRefresher();
        permissionsTableComponent.setVisible(false);
    }

    public String getCurrentUserName(){
        return sheetAppMainController.getCurrentUserName();
    }

    @Override
    public void close(){
        availableSheetsTableComponentController.close();
    }
}
