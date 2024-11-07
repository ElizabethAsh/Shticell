package components.dashboardComponents.commandsPanel.permissionRequestsTable;

import com.google.gson.Gson;
import impl.engine.PendingPermissionDTO;
import impl.ui.PermissionRequestDecisionDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import okhttp3.*;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static util.constants.Constants.RESPOND_TO_SHEET_PERMISSION_REQUEST_URL;

public class PermissionRequestsTableController {
    @FXML private TableView<PendingPermissionDTO> permissionRequestsTable;
    @FXML private TableColumn<PendingPermissionDTO, String> sheetNameColumn;
    @FXML private TableColumn<PendingPermissionDTO, String> requestingUserColumn;
    @FXML private TableColumn<PendingPermissionDTO, String> requestedPermissionColumn;
    @FXML private TableColumn<PendingPermissionDTO, HBox> approveOrDenyColumn;

    private Map<PendingPermissionDTO, String> decisionMap = new LinkedHashMap<>();


    private Stage stage;

    public void initialize() {
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        requestingUserColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        requestedPermissionColumn.setCellValueFactory(new PropertyValueFactory<>("permissionType"));

        approveOrDenyColumn.setCellValueFactory(data -> {
            PendingPermissionDTO pendingPermission = data.getValue();
            CheckBox approveCheckBox = new CheckBox("Approve");
            CheckBox denyCheckBox = new CheckBox("Deny");

            configureCheckBoxListeners(pendingPermission, approveCheckBox, denyCheckBox);

            HBox hbox = new HBox(10);
            hbox.getChildren().addAll(approveCheckBox, denyCheckBox);

            return new SimpleObjectProperty<>(hbox);
        });

    }

    private void configureCheckBoxListeners(PendingPermissionDTO pendingPermission, CheckBox approveCheckBox, CheckBox denyCheckBox) {
        approveCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                decisionMap.put(pendingPermission, "APPROVED");
                denyCheckBox.setSelected(false);
            } else if (!denyCheckBox.isSelected()) {
                decisionMap.remove(pendingPermission);
            }
        });

        denyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                decisionMap.put(pendingPermission, "DENIED");
                approveCheckBox.setSelected(false);
            } else if (!approveCheckBox.isSelected()) {
                decisionMap.remove(pendingPermission);
            }
        });
    }

    public void setStage(Stage stage) { this.stage = stage; }

    public void updatePermissionReviewTable(List<PendingPermissionDTO> permissionsRequests) {
        Platform.runLater(() -> {
            ObservableList<PendingPermissionDTO> items = permissionRequestsTable.getItems();
            items.clear();
            items.addAll(permissionsRequests);
        });
    }

    public void handleOKButtonAction(ActionEvent actionEvent) {
        Gson gson = new Gson();

        for (PendingPermissionDTO request : permissionRequestsTable.getItems()) {
            if (decisionMap.containsKey(request)) {
                String decision = decisionMap.get(request);

                PermissionRequestDecisionDTO dto = new PermissionRequestDecisionDTO(
                        request.getUsername(),
                        request.getSheetName(),
                        request.getPermissionType(),
                        decision,
                        request.getRequestId()
                );

                String json = gson.toJson(dto);

                RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

                HttpClientUtil.runAsyncPut(RESPOND_TO_SHEET_PERMISSION_REQUEST_URL, body, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to update permission for " + request.getUsername() + ": " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {

                            } else {
                                Platform.runLater(() -> {
                                    AlertUtils.showErrorMessage("Failed to update permission for " + request.getUsername() + ": " + response.message());
                                });
                            }
                        } finally {
                            if (response.body() != null) {
                                response.body().close();
                            }
                        }
                    }
                });
            }
        }

        stage.close();
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        stage.close();
    }
}
