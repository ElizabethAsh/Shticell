package components.sheetRoomComponents.sheet;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import java.util.HashMap;
import java.util.Map;

public class UIModel {
    private Map<Label, StringProperty> labelMap;

    public UIModel() {
        labelMap = new HashMap<>();
    }

    private void initializeLabelMap(Label... labels) {
        for (Label cellLabel : labels) {
            StringProperty property = new SimpleStringProperty();
            property.set(cellLabel.getText());
            cellLabel.textProperty().bind(property);
            labelMap.put(cellLabel, property);
        }
    }

    public void updateCellContent(Label label, String content) {
        StringProperty property = labelMap.get(label);
        if (property != null) {
            property.set(content);
        }
    }

    public StringProperty getPropertyForLabel(Label label) {
        return labelMap.get(label);
    }

    public void resetLabelMap(Label... newLabels) {
        labelMap.clear();
        initializeLabelMap(newLabels);
    }

}


