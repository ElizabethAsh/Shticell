package components.sheetRoomComponents.ranges.deleteRangePopup;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;

import java.io.IOException;
import java.util.Set;

public class DeleteRangePopupController {
    @FXML private ComboBox<String> rangesComboBox;
    @FXML private Label errorLabel;

    private SheetRoomMainController mainController;
    private Stage stage;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorLabel.textProperty().bind(errorMessageProperty);
    }

    public void handleSelectButtonAction(ActionEvent actionEvent) {
        String selectedRange = rangesComboBox.getSelectionModel().getSelectedItem();

        if (selectedRange != null) {
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            errorMessageProperty.set("Failed to delete range: " + e.getMessage()));
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> {
                                AlertUtils.showSuccessMessage("Range deleted successfully!");
                                stage.close();
                            });
                        } else {
                            String errorMessage = responseBody != null ? responseBody.string() : "Unknown error";
                            Platform.runLater(() -> errorMessageProperty.set("Error deleting range: " + errorMessage));
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> errorMessageProperty.set("Error deleting range: " + e.getMessage()));
                    }
                }
            };

            mainController.deleteRange(selectedRange, callback);
        } else {
            errorMessageProperty.set("Error deleting range, No range selected.");
        }
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        stage.close();
    }

    public void setRangesComboBox(Set<String> ranges){
        rangesComboBox.getItems().clear();
        rangesComboBox.getItems().addAll(ranges);
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
