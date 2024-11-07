package components.sheetRoomComponents.ranges.addNewRangePopup;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import impl.engine.SheetDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.constants.Constants;

import java.io.IOException;

public class AddNewRangePopupController {
    @FXML private TextField fromBoundariesTextField;
    @FXML private TextField toBoundariesTextField;
    @FXML private TextField rangeNameTextField;
    @FXML private ScrollPane mainContinerScrollPane;

    private SheetRoomMainController mainController;
    private Stage stage;
    
    public void handleOKButtonAction(ActionEvent actionEvent) {
        if (areFieldsValid()) {
            createNewRange();
        }
    }

    private void createNewRange() {
        String fromBoundaries = fromBoundariesTextField.getText();
        String toBoundaries = toBoundariesTextField.getText();
        String rangeName = rangeNameTextField.getText();

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    AlertUtils.showErrorMessage("Failed to add range: " + e.getMessage());
                    fromBoundariesTextField.clear();
                    toBoundariesTextField.clear();
                    rangeNameTextField.clear();
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseString = responseBody.string();
                        SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseString, SheetDTO.class);
                        Platform.runLater(() -> {
                            mainController.updateSheetAfterAddingRange(sheetDTO);
                            AlertUtils.showSuccessMessage("Range added successfully!");
                            stage.close();
                        });
                    } else {
                        String errorMessage = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error adding range: " + errorMessage));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        AlertUtils.showErrorMessage("Error adding range: " + e.getMessage());
                        fromBoundariesTextField.clear();
                        toBoundariesTextField.clear();
                        rangeNameTextField.clear();
                    });
                }
            }
        };

        mainController.addNewRange(rangeName, fromBoundaries, toBoundaries, callback);
    }

    public void handleCancelButtonAction(ActionEvent actionEvent) {
        stage.close();
    }

    private boolean areFieldsValid() {
        boolean isValid = true;

        resetFieldStyles();

        if (fromBoundariesTextField.getText().isEmpty()) {
            fromBoundariesTextField.getStyleClass().add("error-text-field");
            isValid = false;
        }
        if (toBoundariesTextField.getText().isEmpty()) {
            toBoundariesTextField.getStyleClass().add("error-text-field");
            isValid = false;
        }
        if (rangeNameTextField.getText().isEmpty()) {
            rangeNameTextField.getStyleClass().add("error-text-field");
            isValid = false;
        }

        return isValid;
    }

    private void resetFieldStyles() {
        fromBoundariesTextField.getStyleClass().remove("error-text-field");
        toBoundariesTextField.getStyleClass().remove("error-text-field");
        rangeNameTextField.getStyleClass().remove("error-text-field");
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
