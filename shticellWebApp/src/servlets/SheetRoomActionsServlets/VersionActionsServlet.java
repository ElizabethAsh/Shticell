package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.Constants;
import engine.api.Engine;
import impl.engine.NumOfVersionsDTO;
import impl.engine.SheetDTO;
import impl.ui.DisplayVersionSheetRequestDTO;
import impl.ui.SheetUserIdentifierDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name = "VersionActionsServlet", urlPatterns = {"/versionActions/*"})
public class VersionActionsServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);
        String path = request.getPathInfo();

        if (sheetName == null) {
            sendError(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            switch (path) {
                case PATH_GET_SHEET_VERSIONS:
                    handleGetAvailableVersions(response, engine, sheetUserIdentifierDTO);
                    break;
                case PATH_GET_SHEET_BY_VERSION:
                    handleGetSheetByVersion(request, response, engine, sheetUserIdentifierDTO);
                    break;
                case PATH_IS_NEW_VERSION_AVAILABLE:
                    handleIsNewVersionAvailable(response, engine, sheetUserIdentifierDTO);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    try (PrintWriter out = response.getWriter()) {
                        out.println(ERROR_UNKNOWN_ACTION);
                        out.flush();
                    }
                    break;
            }
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }
    }

    private void handleGetAvailableVersions(HttpServletResponse response, Engine engine, SheetUserIdentifierDTO sheetUserIdentifierDTO) throws IOException {
        NumOfVersionsDTO versionsDTO = engine.displayVersions(sheetUserIdentifierDTO);
        sendSuccessResponse(response, versionsDTO);
    }

    private void handleGetSheetByVersion(HttpServletRequest request, HttpServletResponse response, Engine engine, SheetUserIdentifierDTO sheetUserIdentifierDTO) throws IOException {
        String versionNumberParam = request.getParameter(Constants.VERSION_NUMBER);
        if (versionNumberParam == null) {
            sendError(response, ERROR_MISSING_VERSION_NUMBER);
            return;
        }

        try {
            int versionNumber = Integer.parseInt(versionNumberParam);
            SheetDTO sheetDTO = engine.displayVersion(new DisplayVersionSheetRequestDTO(versionNumber), sheetUserIdentifierDTO);
            sendSuccessResponse(response, sheetDTO);
        } catch (NumberFormatException e) {
            sendError(response, ERROR_INVALID_VERSION_NUMBER);
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }
    }

    private void handleIsNewVersionAvailable(HttpServletResponse response, Engine engine, SheetUserIdentifierDTO sheetUserIdentifierDTO) throws IOException {
        try {
            boolean isNewVersionAvailable = engine.checkIsNewVersionAvailable(sheetUserIdentifierDTO);
            sendSuccessResponse(response, isNewVersionAvailable);
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }

    }

    private void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = response.getWriter()) {
            out.println(gson.toJson(data));
            out.flush();
        }
    }

    private void sendError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try (PrintWriter out = response.getWriter()) {
            out.println(errorMessage);
            out.flush();
        }
    }
}
