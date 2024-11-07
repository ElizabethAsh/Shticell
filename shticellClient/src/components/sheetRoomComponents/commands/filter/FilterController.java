package components.sheetRoomComponents.commands.filter;

import exception.InvalidCoordinateException;
import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import impl.engine.SheetDTO;
import impl.engine.UniqueValuesForColumnDTO;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.constants.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.constants.Constants.FILTER_ACTIONS_URL;

public class FilterController {

    @FXML private Button applyFilterButton;
    @FXML private ComboBox<String> columnComboBox;
    @FXML private ListView<CheckBox> valueList;

    private SheetRoomMainController mainController;
    private String from;
    private String to;
    private Map<String, List<String>> selectedColumnValues = new HashMap<>();
    private Stage stage;

    @FXML
    private void initialize() {

        columnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadValuesForSelectedColumn(newValue);
            }
        });
    }

    @FXML
    public void applyFilter(ActionEvent actionEvent) {
        mainController.applyFilter(from, to, selectedColumnValues, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("filter sheet: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO filteredSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        stage.close();
                        mainController.displayFilteredSheet(filteredSheet);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to filter sheet: " + errorMessage));
                }
            }
        });
    }

    @FXML
    public void cancelFilter(ActionEvent actionEvent) {
        stage.close();
    }

    public void setAvailableColumns(ObservableList<String> columns) {
        columnComboBox.setItems(columns);
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    public void setRange(String from, String to) {
        this.from = from;
        this.to = to;
    }

    private void loadValuesForSelectedColumn(String column) {
        String finalUrl = FILTER_ACTIONS_URL  + "/getUniqueValues?column=" + column + "&from_coordinate=" + from + "&to_coordinate=" + to;
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to get unique values: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        UniqueValuesForColumnDTO uniqueValues = Constants.GSON_INSTANCE.fromJson(responseBody, UniqueValuesForColumnDTO.class);
                        Platform.runLater(() -> {
                            valueList.getItems().clear();

                            List<String> selectedValuesForColumn = selectedColumnValues.getOrDefault(column, List.of());

                            for (String value : uniqueValues.getUniqueValues()) {
                                CheckBox checkBox = new CheckBox(value);
                                checkBox.setSelected(selectedValuesForColumn.contains(value));
                                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                                    updateSelectedValues(column, value, newValue);
                                    updateApplyButtonState();
                                });
                                valueList.getItems().add(checkBox);
                            }

                            updateApplyButtonState();
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> AlertUtils.showErrorMessage("Error: " + errorMessage));
                    }
                }
            }
        });
    }

    private void updateSelectedValues(String column, String value, boolean isSelected) {
        List<String> selectedValues = selectedColumnValues.getOrDefault(column, new ArrayList<>());

        if (isSelected) {
            if (!selectedValues.contains(value)) {
                selectedValues.add(value);
            }
        } else {
            selectedValues.remove(value);
        }

        if (selectedValues.isEmpty()) {
            selectedColumnValues.remove(column);
        } else {
            selectedColumnValues.put(column, selectedValues);
        }
    }

    private void updateApplyButtonState() {
        boolean anySelectedInCurrentView = valueList.getItems().stream().anyMatch(CheckBox::isSelected);
        boolean anySelectedInOtherColumns = selectedColumnValues.values().stream().anyMatch(list -> !list.isEmpty());
        applyFilterButton.setDisable(!anySelectedInCurrentView && !anySelectedInOtherColumns);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
