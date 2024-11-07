package components.sheetRoomComponents.dynamicAnalysis;

import components.sheetRoomComponents.sheetRoom.SheetRoomMainController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import util.alert.AlertUtils;

public class DynamicAnalysisController {

    @FXML private TextField stepField;
    @FXML private Button dynamicChangeButton;
    @FXML private Slider dynamicSlider;
    @FXML private TextField minField;
    @FXML private TextField maxField;

    private SheetRoomMainController mainController;

    @FXML private void initialize() {
        dynamicSlider.setDisable(true);
    }

    public void setMainController(SheetRoomMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void dynamicChangeListener(ActionEvent actionEvent) {
        if (dynamicChangeButton.getText().equals("Dynamic Change Button")) {
            startDynamicAnalysis();
        } else {
            stopDynamicAnalysis();
        }
    }

    public void bindComponents(SimpleBooleanProperty isDynamicChangeAllowed) {
        dynamicChangeButton.disableProperty().bind(isDynamicChangeAllowed.not());
        minField.disableProperty().bind(isDynamicChangeAllowed.not());
        maxField.disableProperty().bind(isDynamicChangeAllowed.not());
        stepField.disableProperty().bind(isDynamicChangeAllowed.not());
    }

    private void startDynamicAnalysis() {
        if (areAnalysisFieldsEmpty()) {
            AlertUtils.showErrorMessage("All fields must be filled out to proceed.");
            showInvalidInputBorders();
            dynamicSlider.setDisable(true);
            return;
        }

        if (areAnalysisFieldsCorrect()) {
            double minValue = Double.parseDouble(minField.getText());
            double maxValue = Double.parseDouble(maxField.getText());
            double stepSize = Double.parseDouble(stepField.getText());

            configureDynamicSlider(minValue, maxValue, stepSize);

            mainController.startDynamicAnalysis();
            dynamicChangeButton.setText("Exit Dynamic Change");
            clearTextFieldBorders();
        }
    }

    private boolean areAnalysisFieldsEmpty() {
        return minField.getText().isEmpty() ||
                maxField.getText().isEmpty() ||
                stepField.getText().isEmpty();
    }

    private boolean areAnalysisFieldsCorrect() throws NumberFormatException {
        try {
            double minValue = Double.parseDouble(minField.getText());
            double maxValue = Double.parseDouble(maxField.getText());
            double stepSize = Double.parseDouble(stepField.getText());

            return validateInput(minValue, maxValue, stepSize);
        } catch (NumberFormatException e) {
            AlertUtils.showErrorMessage("Please enter valid numeric values.");
            showInvalidInputBorders();
            return false;
        }
    }

    public boolean validateInput(double minValue, double maxValue, double stepSize) {
        if (minValue >= maxValue) {
            AlertUtils.showErrorMessage("Error: Min value must be less than Max value.");
            return false;
        }

        double range = maxValue - minValue;
        if (stepSize <= 0 || stepSize > range) {
            AlertUtils.showErrorMessage("Error: Step size must be positive and less than or equal to the range between Min and Max values.");
            return false;
        }

        if ((range % stepSize) != 0) {
            AlertUtils.showErrorMessage("Error: Step size must divide the range evenly.");
            return false;
        }
        return true;
    }

    private void stopDynamicAnalysis() {
        mainController.updateIsDynamicChangeAllowed(false);
        mainController.stopDynamicAnalysis();
        dynamicChangeButton.setText("Dynamic Change Button");
        dynamicSlider.setDisable(true);
        resetAnalysisFields();
    }

    private void resetAnalysisFields() {
        minField.clear();
        maxField.clear();
        stepField.clear();
        clearTextFieldBorders();
    }

    private void configureDynamicSlider(double minValue, double maxValue, double stepSize) {
        dynamicSlider.setDisable(false);
        dynamicSlider.setMin(minValue);
        dynamicSlider.setMax(maxValue);
        dynamicSlider.setMajorTickUnit(stepSize);
        dynamicSlider.setBlockIncrement(stepSize);
        dynamicSlider.setValue(minValue);

        dynamicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double roundedValue = Math.round(newVal.doubleValue() / stepSize) * stepSize;
            dynamicSlider.setValue(roundedValue);
            mainController.updateCellValueInDynamicAnalysis(String.valueOf(roundedValue));
        });
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showInvalidInputBorders() {
        if (minField.getText().isEmpty() || !isNumeric(minField.getText())) {
            minField.setStyle("-fx-border-color: red;");
        }

        if (maxField.getText().isEmpty() || !isNumeric(maxField.getText())) {
            maxField.setStyle("-fx-border-color: red;");
        }

        if (stepField.getText().isEmpty() || !isNumeric(stepField.getText())) {
            stepField.setStyle("-fx-border-color: red;");
        }
    }

    private void clearTextFieldBorders() {
        minField.setStyle(null);
        maxField.setStyle(null);
        stepField.setStyle(null);
    }
}
