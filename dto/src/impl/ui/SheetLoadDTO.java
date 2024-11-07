package impl.ui;

import java.io.InputStream;

public class SheetLoadDTO {
    private final InputStream fileStream;
    private final String uploaderUsername;

    public SheetLoadDTO(InputStream fileStream, String uploaderUsername) {
        this.fileStream = fileStream;
        this.uploaderUsername = uploaderUsername;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public String getUploaderUserName() {
        return uploaderUsername;
    }
}
