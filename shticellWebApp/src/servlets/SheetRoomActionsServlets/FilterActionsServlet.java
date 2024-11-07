package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.api.Engine;
import impl.engine.AvailableColumnsForFilterDTO;
import impl.engine.SheetDTO;
import impl.engine.UniqueValuesForColumnDTO;
import impl.ui.FilterRequestDTO;
import impl.ui.RangeBoundariesForSortingOrFilterDTO;
import impl.ui.SheetUserIdentifierDTO;
import impl.ui.UniqueValuesForColumnRequestDTO;
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

@WebServlet(name = "FilterActionsServlet", urlPatterns = {"/filter/*"})
public class FilterActionsServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String pathInfo = request.getPathInfo();
        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            sendError(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        if ("/getColumns".equals(pathInfo)) {
            handleGetColumns(request, response, username, sheetName);
        } else if ("/getUniqueValues".equals(pathInfo)) {
            handleGetUniqueValues(request, response, username, sheetName);
        } else {
            sendError(response, "Error: Invalid action.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();
        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            sendError(response, ERROR_NO_SHEET_SELECTED );
            return;
        }

        if ("/filter".equals(pathInfo)) {
            handleFilter(request, response, username, sheetName);
        } else {
            sendError(response, "Error: Invalid action.");
        }
    }

    private void handleGetColumns(HttpServletRequest request, HttpServletResponse response, String username, String sheetName) throws IOException {
        String from = request.getParameter(FROM_COORDINATE);
        String to = request.getParameter(TO_COORDINATE);

        if (from == null || to == null) {
            sendError(response, "Error: Missing parameters.");
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            AvailableColumnsForFilterDTO availableColumns = engine.getAvailableColumnsForFilter(
                    new RangeBoundariesForSortingOrFilterDTO(from, to), sheetUserIdentifierDTO);
            sendSuccessResponse(response, availableColumns);
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }
    }

    private void handleGetUniqueValues(HttpServletRequest request, HttpServletResponse response, String username, String sheetName) throws IOException {
        String column = request.getParameter(COLUMN);
        String fromCoordinate = request.getParameter(FROM_COORDINATE);
        String toCoordinate = request.getParameter(TO_COORDINATE);

        if (column == null || fromCoordinate == null || toCoordinate == null) {
            sendError(response, "Error: Missing parameters.");
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            UniqueValuesForColumnDTO uniqueValues = engine.getDistinctValuesForColumnInRange(
                    new UniqueValuesForColumnRequestDTO(column, fromCoordinate, toCoordinate), sheetUserIdentifierDTO);
            sendSuccessResponse(response, uniqueValues);
        } catch (Exception e) {
            sendError(response, e.getMessage());
        }
    }

    private void handleFilter(HttpServletRequest request, HttpServletResponse response, String username, String sheetName) throws IOException {
        FilterRequestDTO filterRequestDTO = gson.fromJson(request.getReader(), FilterRequestDTO.class);
        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            SheetDTO filteredSheet = engine.filterSheet(filterRequestDTO, sheetUserIdentifierDTO);
            sendSuccessResponse(response, filteredSheet);
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
