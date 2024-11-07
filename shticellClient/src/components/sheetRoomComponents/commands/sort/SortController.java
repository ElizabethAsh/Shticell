package components.sheetRoomComponents.commands.sort;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import impl.engine.SheetDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.alert.AlertUtils;
import util.constants.Constants;
import java.io.IOException;
import java.util.ArrayList;

public class SortController {

    @FXML private VBox sortLevelsVBox;
    @FXML private Button okButton;
    @FXML private Button addColumnButton;
    @FXML private ComboBox<String> columnComboBox;

    private ArrayList<ComboBox<String>> sortComboBoxes = new ArrayList<>();
    private SheetRoomMainController mainController;
    private String from;
    private String to;
    private ArrayList<String> selectedColumns = new ArrayList<>();
    private ObservableList<String> originalColumns;
    private Stage stage;

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        okButton.disableProperty().bind(columnComboBox.getSelectionModel().selectedItemProperty().isNull());
    }

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    public void applySort(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();

        selectedColumns.clear();
        for (ComboBox<String> comboBox : sortComboBoxes) {
            if (comboBox.getValue() != null && !comboBox.getValue().isEmpty()) {
                selectedColumns.add(comboBox.getValue());
            }
        }

        mainController.applySort(from, to, selectedColumns, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to sort rows: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO sortedSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        stage.close();
                        mainController.displaySortedSheet(sortedSheet);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> AlertUtils.showErrorMessage("Failed to sort rows: " + errorMessage));
                }
            }
        });
    }

    @FXML
    public void cancelSort(javafx.event.ActionEvent actionEvent) {
        stage.close();
    }

    public void setAvailableColumns(ObservableList<String> columns) {
        this.originalColumns = FXCollections.observableArrayList(columns);
        columnComboBox.setItems(columns);
        sortComboBoxes.add(columnComboBox);
        addColumnButton.setDisable(true);

        columnComboBox.valueProperty().addListener((observable, oldValue, newValue) -> handleComboBoxValueChange(columnComboBox, oldValue, newValue));
    }

    public void setRange(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public void addColumnListener(ActionEvent actionEvent) {
        HBox newSortLevel = new HBox();
        Label andThenLabel = new Label("And then by:");

        ObservableList<String> availableColumns = FXCollections.observableArrayList(originalColumns);
        for (ComboBox<String> comboBox : sortComboBoxes) {
            String selectedValue = comboBox.getValue();
            if (selectedValue != null) {
                availableColumns.remove(selectedValue);
            }
        }

        if (availableColumns.isEmpty()) {
            addColumnButton.setDisable(true);
            return;
        }

        ComboBox<String> newComboBox = new ComboBox<>(availableColumns);

        newComboBox.setPromptText("Select another column");
        newComboBox.setPrefWidth(150);

        newComboBox.valueProperty().addListener((observable, oldValue, newValue) -> handleComboBoxValueChange(newComboBox, oldValue, newValue));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> removeSortLevel(newSortLevel, newComboBox));

        newSortLevel.getChildren().addAll(andThenLabel, newComboBox, deleteButton);
        newSortLevel.setSpacing(10);

        sortLevelsVBox.getChildren().add(newSortLevel);
        sortComboBoxes.add(newComboBox);

        addColumnButton.setDisable(true);
    }

    private void removeSortLevel(HBox sortLevel, ComboBox<String> comboBox) {
        sortLevelsVBox.getChildren().remove(sortLevel);
        sortComboBoxes.remove(comboBox);

        if (comboBox.getValue() != null) {
            for (ComboBox<String> otherComboBox : sortComboBoxes) {
                if (!otherComboBox.getItems().contains(comboBox.getValue())) {
                    otherComboBox.getItems().add(comboBox.getValue());
                }
            }
        }
    }

    private void addComboBoxValueChangeListener(ComboBox<String> comboBox) {
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                addColumnButton.setDisable(false);
            } else {
                addColumnButton.setDisable(true);
            }
        });
    }

    private void handleComboBoxValueChange(ComboBox<String> comboBox, String oldValue, String newValue) {
        if (oldValue != null) {
            for (ComboBox<String> otherComboBox : sortComboBoxes) {
                if (!otherComboBox.equals(comboBox) && !otherComboBox.getItems().contains(oldValue)) {
                    otherComboBox.getItems().add(oldValue);
                }
            }
        }

        if (newValue != null) {
            for (ComboBox<String> otherComboBox : sortComboBoxes) {
                if (!otherComboBox.equals(comboBox) && otherComboBox.getItems().contains(newValue)) {
                    otherComboBox.getItems().remove(newValue);
                }
            }
        }

        checkAddColumnButtonState();
    }

    private void checkAddColumnButtonState() {
        int availableColumnsCount = originalColumns.size();

        for (ComboBox<String> comboBox : sortComboBoxes) {
            availableColumnsCount -= comboBox.getSelectionModel().getSelectedItem() != null ? 1 : 0;
        }

        ComboBox<String> lastComboBox = sortComboBoxes.get(sortComboBoxes.size() - 1);
        boolean lastComboBoxHasValue = lastComboBox.getValue() != null && !lastComboBox.getValue().isEmpty();

        addColumnButton.setDisable(availableColumnsCount < 1 || !lastComboBoxHasValue);
    }
}
