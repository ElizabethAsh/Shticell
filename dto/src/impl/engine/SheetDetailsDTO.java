package impl.engine;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SheetDetailsDTO {
    private String sheetName;
    private String usernameNameOfUploader;
    private String sheetSize;
    private String userPermission;
    private transient StringProperty userPermissionProperty;

    public SheetDetailsDTO(String sheetName, String userPermission, String usernameNameOfUploader, String sheetSize) {
        this.sheetName = sheetName;
        this.userPermission = userPermission;
        this.usernameNameOfUploader = usernameNameOfUploader;
        this.sheetSize = sheetSize;
        this.userPermissionProperty = new SimpleStringProperty(userPermission);

    }

    public String getSheetName() { return sheetName; }

    public String getUserPermission() {
        return userPermission;
    }

    public StringProperty userPermissionProperty() {
        if (userPermissionProperty == null) {
            userPermissionProperty = new SimpleStringProperty(userPermission);
        }
        return userPermissionProperty;
    }


    public String getUsernameNameOfUploader() { return usernameNameOfUploader; }

    public String getSheetSize() { return sheetSize; }

    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
        if (userPermissionProperty != null) {
            this.userPermissionProperty.set(userPermission);
        }
    }
}
