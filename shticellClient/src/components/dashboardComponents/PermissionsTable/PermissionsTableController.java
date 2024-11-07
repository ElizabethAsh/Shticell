package components.dashboardComponents.PermissionsTable;

import impl.engine.UserPermissionDTO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

public class PermissionsTableController {
    @FXML private TableColumn<UserPermissionDTO, String> usernameColumn;
    @FXML private TableColumn<UserPermissionDTO, String> PermissionTypeColumn;
    @FXML private TableColumn<UserPermissionDTO, String> ApprovalStatusColumn;
    @FXML private TableView<UserPermissionDTO> permissionsTable;

    private Timer permissionsTableTimer;
    private TimerTask permissionsTableRefresher;

    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        PermissionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        ApprovalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
    }

    public void updatePermissionsTable(List<UserPermissionDTO> permissions) {
        ObservableList<UserPermissionDTO> currentItems = permissionsTable.getItems();

        if (!currentItems.equals(permissions)){
            Platform.runLater(() -> {
                ObservableList<UserPermissionDTO> items = permissionsTable.getItems();
                items.clear();
                items.addAll(permissions);
            });
        }
    }

    public void startPermissionsTableRefresher(String sheetName) {
        permissionsTableRefresher = new PermissionsTableRefresher(sheetName, this::updatePermissionsTable);
        permissionsTableTimer = new Timer();
        permissionsTableTimer.schedule(permissionsTableRefresher, 0, 5000);
    }

    public void stopPermissionsTableRefresher() {
        if (permissionsTableRefresher != null) {
            permissionsTableRefresher.cancel();
        }
        if (permissionsTableTimer != null) {
            permissionsTableTimer.cancel();
        }
    }

}
