package servlets.dashBoardServlets;

import com.google.gson.Gson;
import engine.api.Engine;
import impl.engine.PendingPermissionDTO;
import impl.engine.UserPermissionDTO;
import impl.ui.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.*;

@WebServlet(name = "PermissionsServlet", urlPatterns = {"/permissions/*"})
public class PermissionsServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String path = request.getPathInfo();

        switch (path) {
            case PATH_GET_USER_AVAILABLE_PERMISSIONS:
                getUserAvailablePermissionsForSheet(request, response);
                break;
            case PATH_GET_USER_PENDING_REQUESTS:
                getUserPendingPermissionRequests(request, response);
                break;
            case PATH_GET_SHEET_PERMISSIONS_TABLE:
                getSheetPermissionsTable(request, response);
                break;
            default:
                sendError(response, ERROR_UNKNOWN_ACTION);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();

        switch (path) {
            case PATH_SUBMIT_PERMISSION_REQUEST:
                submitSheetPermissionRequest(request, response);
                break;
            default:
                sendError(response, ERROR_UNKNOWN_ACTION);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();

        switch (path) {
            case PATH_RESPOND_TO_PERMISSION_REQUEST:
                respondToSheetPermissionRequest(request, response);
                break;
            default:
                sendError(response, ERROR_UNKNOWN_ACTION);
        }
    }

    private void getUserAvailablePermissionsForSheet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = request.getParameter(CURRENT_SHEET_NAME);
        String username = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        if (sheetName == null || username == null) {
            sendError(response, ERROR_MISSING_PARAMETERS);
            return;
        }

        SheetUserIdentifierDTO dto = new SheetUserIdentifierDTO(sheetName, username);
        try {
            List<String> availablePermissions = engine.getAvailablePermissionsForUser(dto);
            if (availablePermissions.isEmpty()) {
                sendEmptyResponse(response, "No available permissions to request.");
            } else {
                sendSuccessResponse(response, availablePermissions);
            }
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }
    }

    private void getUserPendingPermissionRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            UserNameDTO userNameDTO = new UserNameDTO(username);
            List<PendingPermissionDTO> pendingRequests = engine.getPendingPermissions(userNameDTO);

            if (pendingRequests == null || pendingRequests.isEmpty()) {
                sendEmptyResponse(response, "No pending permission requests.");
            } else {
                sendSuccessResponse(response, pendingRequests);
            }
        } catch (RuntimeException e) {
            sendError(response, "Failed to retrieve pending requests: " + e.getMessage());
        }
    }

    private void getSheetPermissionsTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = request.getParameter(CURRENT_SHEET_NAME);
        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            CurrentSheetNameDTO sheetNameDTO = new CurrentSheetNameDTO(sheetName);
            List<UserPermissionDTO> permissions = engine.getPermissionsTableForSheet(sheetNameDTO);

            sendSuccessResponse(response, permissions);
        } catch (RuntimeException e) {
            sendError(response, "Failed to retrieve permissions table: " + e.getMessage());
        }
    }

    private void submitSheetPermissionRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        String sheetName = request.getParameter(CURRENT_SHEET_NAME);
        String permissionType = request.getParameter(PERMISSION_TYPE);
        String username = SessionUtils.getUsername(request);

        if (sheetName == null || permissionType == null || username == null) {
            sendError(response, ERROR_MISSING_PARAMETERS);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        PermissionRequestDTO requestDTO = new PermissionRequestDTO(sheetName, username, permissionType);

        try {
            engine.requestPermission(requestDTO);
            sendSuccessResponse(response, "Request submitted successfully.");
        } catch (RuntimeException e) {
            sendError(response, e.getMessage());
        }
    }

    private void respondToSheetPermissionRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PermissionRequestDecisionDTO decisionDTO = gson.fromJson(request.getReader(), PermissionRequestDecisionDTO.class);

        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            engine.handlePermissionDecision(decisionDTO);
            sendSuccessResponse(response, "Decision processed successfully.");
        } catch (RuntimeException e) {
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

    private void sendEmptyResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        try (PrintWriter out = response.getWriter()) {
            out.println(gson.toJson(message));
            out.flush();
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
            out.flush();
        }
    }
}
