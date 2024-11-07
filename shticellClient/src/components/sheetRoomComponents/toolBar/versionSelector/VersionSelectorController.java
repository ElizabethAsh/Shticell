package components.sheetRoomComponents.toolBar.versionSelector;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import impl.engine.SheetDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.constants.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Set;

import static util.constants.Constants.GET_SHEET_BY_VERSION_URL;


public class VersionSelectorController {
    @FXML private Label errorLabel;
    @FXML private ComboBox<Integer> versionComboBox;

    SheetRoomMainController mainController;
    private Stage stage;

    public void setVersions(Set<Integer> versions) {
        versionComboBox.getItems().clear();
        versionComboBox.getItems().addAll(versions);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        stage.close();
    }

    public void handleSelectButtonAction(ActionEvent actionEvent) {
        Integer selectedVersion = versionComboBox.getValue();

        if (selectedVersion != null) {
            fetchSheetByVersion(selectedVersion);
        } else {
            errorLabel.setText("Please select a version.");
        }
    }

    private void fetchSheetByVersion(Integer version) {
        String finalUrl = GET_SHEET_BY_VERSION_URL + "?version_number=" + version;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to retrieve version: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                        Platform.runLater(() -> {
                            stage.close();
                            mainController.showSelectedVersionSheet(sheetDTO, version);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> errorLabel.setText("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }
}
