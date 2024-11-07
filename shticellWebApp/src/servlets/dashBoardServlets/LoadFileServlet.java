package servlets.dashBoardServlets;

import engine.api.Engine;
import exception.InvalidCoordinateException;
import exception.RangeExistsException;
import exception.SheetConversionException;
import impl.ui.SheetLoadDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.xml.bind.JAXBException;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload-file"})
@MultipartConfig
public class LoadFileServlet extends HttpServlet {
    @Override
    protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            for (Part part : request.getParts()) {
                if ("application/xml".equalsIgnoreCase(part.getContentType())) {
                    loadFileToEngine(part.getInputStream(), engine, usernameFromSession);
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.println("File uploaded and processed successfully.");
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.println("No valid XML file found. Please upload a file with the correct content type (application/xml).");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.println(e.getMessage());
        }

    }

    private void loadFileToEngine(InputStream fileStream, Engine engine, String userName) throws IOException, InvalidCoordinateException, JAXBException, SheetConversionException, RangeExistsException {
        SheetLoadDTO loadDTO = new SheetLoadDTO(fileStream, userName);
        engine.loadSheet(loadDTO);
    }
}
