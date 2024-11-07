package components.dashboardComponents.commandsPanel.permissionRequest;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import okhttp3.*;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;

import static util.constants.Constants.*;

public class PermissionRequestController {
    @FXML private ComboBox<String> permissionOptionsBox;
    @FXML private Label titleLabel;

    private String sheetName;
    private Stage stage;

    public void handleOKButtonAction(ActionEvent actionEvent) {
        String selectedPermission = permissionOptionsBox.getValue();

        if (selectedPermission != null && !selectedPermission.isEmpty()) {
            RequestBody formBody = new FormBody.Builder()
                    .add(CURRENT_SHEET_NAME, sheetName)
                    .add(PERMISSION_TYPE, selectedPermission)
                    .build();


            HttpClientUtil.runAsyncWithBody(SUBMIT_SHEET_PERMISSION_REQUEST_URL, formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to request permission: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                AlertUtils.showSuccessMessage("Permission requested successfully!");
                                stage.close();
                            });
                        } else {
                            Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to request permission: " + response.message()));
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> AlertUtils.showErrorMessage("An error occurred while processing the response: " + e.getMessage()));
                    }
                }
            });
        } else {
            AlertUtils.showErrorMessage("Please select a permission.");
        }
    }


    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
        titleLabel.setText("Request permission for: " + sheetName);
    }

    public void updatePermissionOptions(List<String> availablePermissions) {
        permissionOptionsBox.getItems().clear();
        permissionOptionsBox.getItems().addAll(availablePermissions);
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
