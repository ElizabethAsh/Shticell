package impl.ui;

public class SheetUserIdentifierDTO {
    String sheetName;
    String currentUsername;

    public SheetUserIdentifierDTO(String sheetName, String currentUsername) {
        this.sheetName = sheetName;
        this.currentUsername = currentUsername;
    }

    public String getSheetName() { return sheetName; }

    public String getCurrentUsername() { return currentUsername; }
}
