package components.sheetRoomComponents.ranges;

import components.sheetRoomComponents.ranges.addNewRangePopup.AddNewRangePopupController;
import components.sheetRoomComponents.ranges.deleteRangePopup.DeleteRangePopupController;
import components.sheetRoomComponents.ranges.displayRangePopup.DisplayRangePopupController;
import components.sheetRoomComponents.toolBar.versionSelector.VersionSelectorController;
import exception.RangeInUseException;
import impl.engine.NumOfVersionsDTO;
import impl.engine.RangeDTO;
import impl.engine.SheetDTO;
import impl.ui.RangeNameDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static util.constants.Constants.*;

public class rangesController  {
    @FXML private Button DeleteRangeButton;
    @FXML private Button DisplayRangeButton;
    @FXML private Button addNewRangeButton;

    private SheetRoomMainController mainController;
    private static final String DEFAULT_PROMPT = "Select Range";

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    public void updateButtonsState(String userPermission) {
        boolean isReader = userPermission.equals("READER");

        DeleteRangeButton.disableProperty().bind((Bindings.createBooleanBinding(() -> isReader)));
        addNewRangeButton.disableProperty().bind(Bindings.createBooleanBinding(() -> isReader));
    }

    @FXML
    public void displayRangeListener(ActionEvent actionEvent) {
        mainController.resetSelectedCell();

        String urlWithAction = GET_AVAILABLE_RANGES_URL + "?action=display";

        HttpClientUtil.runAsync(urlWithAction, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get versions: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseString = responseBody.string();
                        RangeNameDTO[] rangeArray = GSON_INSTANCE.fromJson(responseString, RangeNameDTO[].class);
                        Set<String> rangeNames = extractRangeNames(rangeArray);
                        Platform.runLater(() -> openDisplayRangePopup(rangeNames));
                    } else {
                        String errorMessage = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error fetching ranges: " + errorMessage));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get ranges: " + e.getMessage()));
                }
            }
        });
    }

    @FXML
    public void DeleteRangeListener(ActionEvent actionEvent){
        mainController.resetSelectedCell();
        String urlWithAction = GET_AVAILABLE_RANGES_URL + "?action=delete";

        HttpClientUtil.runAsync(urlWithAction, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get versions: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful() && responseBody != null) {
                        String responseString = responseBody.string();
                        RangeNameDTO[] rangeArray = GSON_INSTANCE.fromJson(responseString, RangeNameDTO[].class);
                        Set<String> rangeNames = extractRangeNames(rangeArray);
                        Platform.runLater(() -> openDeleteRangePopup(rangeNames));
                    } else {
                        String errorMessage = responseBody != null ? responseBody.string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error fetching ranges: " + errorMessage));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get ranges: " + e.getMessage()));
                }
            }
        });

    }

    private void openDisplayRangePopup(Set<String> ranges) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/ranges/displayRangePopup/display-range-popup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Display Range");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            DisplayRangePopupController displayRangePopupController = loader.getController();
            displayRangePopupController.setStage(popupStage);
            displayRangePopupController.setMainController(mainController);
            displayRangePopupController.setRangesComboBox(ranges);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open delete range popup.");
        }
    }

    private void openDeleteRangePopup(Set<String> ranges) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/ranges/deleteRangePopup/delete-range-popup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Delete Range");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            DeleteRangePopupController deleteRangePopupController = loader.getController();
            deleteRangePopupController.setStage(popupStage);
            deleteRangePopupController.setMainController(mainController);
            deleteRangePopupController.setRangesComboBox(ranges);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open delete range popup.");
        }
    }

    @FXML
    public void addNewRangeListener(ActionEvent actionEvent) {
        mainController.resetSelectedCell();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sheetRoomComponents/ranges/addNewRangePopup/add-new-range-popup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Add New Range");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(mainController.getPrimaryStage());

            AddNewRangePopupController addNewRangePopupController = loader.getController();
            addNewRangePopupController.setStage(popupStage);
            addNewRangePopupController.setMainController(mainController);

            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showErrorMessage("Failed to open permission request popup.");
        }
    }

    private Set<String> extractRangeNames(RangeNameDTO[] rangeArray) {
        return Arrays.stream(rangeArray)
                .map(RangeNameDTO::getName)
                .collect(Collectors.toSet());
    }

    public static String[] extractRangeBoundaries(String rangeBoundaries) throws Exception {
        String[] boundaries = rangeBoundaries.split("\\.\\.");

        if (boundaries.length != 2) {
            throw new Exception("Invalid range format. Please use the format: <top-left cell>..<bottom-right cell>");
        }

        return boundaries;
    }
}
