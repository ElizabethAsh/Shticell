package components.dashboardComponents.commandsPanel;

import components.dashboardComponents.commandsPanel.permissionRequest.PermissionRequestController;
import components.dashboardComponents.commandsPanel.permissionRequestsTable.PermissionRequestsTableController;
import components.dashboardComponents.dashboardRoom.DashboardController;
import impl.engine.PendingPermissionDTO;
import impl.engine.SheetDTO;
import impl.engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;

import static util.constants.Constants.*;

public class CommandsPanelController {
    private DashboardController mainController;

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }


    public void viewSheetListener(ActionEvent actionEvent) throws IOException {
        SheetDetailsDTO selectedSheet = mainController.getSelectedSheetFromOverview();
        if (selectedSheet == null) {
            AlertUtils.showErrorMessage("Please select a sheet to view.");
            return;
        }

        String sheetName = selectedSheet != null ? selectedSheet.getSheetName() : null;

        String finalUrl = HttpUrl.parse(VIEW_SHEET_URL).newBuilder()
                .addQueryParameter(CURRENT_SHEET_NAME, sheetName)
                .build().toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("View sheet failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseBodyString  = response.body() != null ? response.body().string() : "";
                        SheetDTO sheetDTO = GSON_INSTANCE.fromJson(responseBodyString, SheetDTO.class);
                        Platform.runLater(() -> {
                            mainController.loadSheet(sheetDTO);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to load sheet: " + errorMessage));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to process the response: " + e.getMessage()));
                }
            }
        });
    }

    public void requestPermissionListener(ActionEvent actionEvent) {
        SheetDetailsDTO selectedSheet = mainController.getSelectedSheetFromOverview();

        if (selectedSheet == null) {
            AlertUtils.showErrorMessage("Please select a sheet.");
            return;
        }

        String sheetName = selectedSheet.getSheetName();
        String currentPermission = selectedSheet.getUserPermission();

        fetchAvailablePermissionsFromServer(sheetName, currentPermission);
    }

    public void AcknowledgementOrDenyPermissionRequestListener(ActionEvent actionEvent) {
        String finalUrl = HttpUrl.parse(GET_USER_PENDING_PERMISSIONS_URL).newBuilder().build().toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Failed to retrieve pending permissions: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.code() == 200 && responseBody != null) {
                        String jsonResponse = responseBody.string();
                        List<PendingPermissionDTO> pendingRequests = GSON_INSTANCE.fromJson(jsonResponse,
                                new com.google.gson.reflect.TypeToken<List<PendingPermissionDTO>>() {}.getType());

                        Platform.runLater(() -> openAcknowledgementOrDenyPermissionTablePopup(pendingRequests));
                    } else if (response.code() == 204) {
                        Platform.runLater(() -> AlertUtils.showSuccessMessage("No pending permission requests."));
                    } else {
                        String errorMessage = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to retrieve pending permissions: " + errorMessage));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to process pending permissions response: " + e.getMessage()));
                }
            }
        });
    }

    private void fetchAvailablePermissionsFromServer(String sheetName, String currentPermission) {
        String finalUrl = HttpUrl.parse(GET_USER_AVAILABLE_PERMISSIONS_URL)
                .newBuilder()
                .addQueryParameter(CURRENT_SHEET_NAME, sheetName)
                .build().toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    AlertUtils.showErrorMessage("Failed to retrieve available permissions: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String jsonResponse = response.body() != null ? response.body().string() : "";
                        List<String> availablePermissions = GSON_INSTANCE.fromJson(jsonResponse,
                                new com.google.gson.reflect.TypeToken<List<String>>() {}.getType());

                        if (!availablePermissions.isEmpty()) {
                            Platform.runLater(() -> {
                                openPermissionRequestPopup(sheetName, availablePermissions);
                            });
                        } else {
                            Platform.runLater(() -> {
                                AlertUtils.showErrorMessage("No permissions available to request.");
                            });
                        }
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to retrieve available permissions: " + errorMessage));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to retrieve available permissions: " + e.getMessage()));
                }
            }
        });
    }


    // חלונות הפופאפ שנפתחים כתוצאה מבקשת הרשאה או אישור בקשות למשתמשים
    private void openPermissionRequestPopup(String sheetName, List<String> currentPermission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dashboardComponents/commandsPanel/permissionRequest/permission-request.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Request Permission for " + sheetName);
            popupStage.setScene(new Scene(root));

            PermissionRequestController controller = loader.getController();
            controller.setSheetName(sheetName);
            controller.setStage(popupStage);
            controller.updatePermissionOptions(currentPermission);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }

    private void openAcknowledgementOrDenyPermissionTablePopup(List<PendingPermissionDTO> pendingRequests) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dashboardComponents/commandsPanel/permissionRequestsTable/permission-requests-table.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));

            PermissionRequestsTableController controller = loader.getController();
            controller.setStage(popupStage);
            controller.updatePermissionReviewTable(pendingRequests);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }
}

