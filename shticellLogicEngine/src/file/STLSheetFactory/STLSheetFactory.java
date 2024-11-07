package file.STLSheetFactory;

import exception.XmlProcessingException;
import file.schema.generated.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;

public class STLSheetFactory {

    private final static String JAXB_XML_PACKAGE_NAME = "file.schema.generated";

    public static STLSheet createStlSheet(String filePath) throws XmlProcessingException {
        return null;
    }


    public static STLSheet createStlSheet(InputStream fileStream) {
        try {
            return deserializeFrom(fileStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Error processing XML file" + e.getMessage());
        }

    }

    private static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }

}
