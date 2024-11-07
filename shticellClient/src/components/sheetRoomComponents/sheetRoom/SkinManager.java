package components.sheetRoomComponents.sheetRoom;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import util.constants.Constants;

public class SkinManager {
    public static void applySkin(int skinNumber, BorderPane root, FlowPane commandsComponent, FlowPane rangesComponent, VBox tablePane, VBox actionBarComponent) {
//        root.getStylesheets().clear();
        commandsComponent.getStylesheets().clear();
        rangesComponent.getStylesheets().clear();
        tablePane.getStylesheets().clear();
        actionBarComponent.getStylesheets().clear();

        switch (skinNumber) {
            case 1:
                commandsComponent.getStylesheets().add(SkinManager.class.getResource(Constants.COMMANDS_STYLE_1).toExternalForm());
                rangesComponent.getStylesheets().add(SkinManager.class.getResource(Constants.RANGES_STYLE_1).toExternalForm());
                tablePane.getStylesheets().add(SkinManager.class.getResource(Constants.SHEET_STYLE_1).toExternalForm());
                actionBarComponent.getStylesheets().add(SkinManager.class.getResource(Constants.TOOL_BAR_STYLE_1).toExternalForm());
                root.setStyle("-fx-background-color: #FFFFFF;");
                tablePane.setStyle("-fx-background-color: #FFFFFF;");
                break;
            case 2:
                commandsComponent.getStylesheets().add(SkinManager.class.getResource(Constants.COMMANDS_STYLE_2).toExternalForm());
                rangesComponent.getStylesheets().add(SkinManager.class.getResource(Constants.RANGES_STYLE_2).toExternalForm());
                tablePane.getStylesheets().add(SkinManager.class.getResource(Constants.SHEET_STYLE_2).toExternalForm());
                actionBarComponent.getStylesheets().add(SkinManager.class.getResource(Constants.TOOL_BAR_STYLE_2).toExternalForm());
                root.setStyle("-fx-background-color: #FFDB58;");
                tablePane.setStyle("-fx-background-color: #F5F5DC;");
                break;
            case 3:
                commandsComponent.getStylesheets().add(SkinManager.class.getResource(Constants.COMMANDS_STYLE_3).toExternalForm());
                rangesComponent.getStylesheets().add(SkinManager.class.getResource(Constants.RANGES_STYLE_3).toExternalForm());
                tablePane.getStylesheets().add(SkinManager.class.getResource(Constants.SHEET_STYLE_3).toExternalForm());
                actionBarComponent.getStylesheets().add(SkinManager.class.getResource(Constants.TOOL_BAR_STYLE_3).toExternalForm());
                root.setStyle("-fx-background-color: #A0522D;");
                tablePane.setStyle("-fx-background-color: #98FB98;");
                break;
        }
    }
}
