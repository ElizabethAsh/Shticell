package components.sheetRoomComponents.ranges.displayRangePopup;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import impl.engine.RangeDTO;
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
import util.constants.Constants;

import java.io.IOException;
import java.util.Set;

public class DisplayRangePopupController {
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
                            errorMessageProperty.set("Failed to display range: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) {
                    try (ResponseBody responseBody = response.body()) {
                        String responseString = responseBody != null ? responseBody.string() : "";
                        RangeDTO selectedRange = Constants.GSON_INSTANCE.fromJson(responseString, RangeDTO.class);
                        Platform.runLater(() -> {
                            mainController.showSelectRange(selectedRange);
                            stage.close();
                        });
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            errorMessageProperty.set("Error displaying range: " + e.getMessage());
                        });
                    }
                }
            };

            mainController.displayRange(selectedRange, callback);
        } else {
            AlertUtils.showErrorMessage("Error displaying range, no range selected.");
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
