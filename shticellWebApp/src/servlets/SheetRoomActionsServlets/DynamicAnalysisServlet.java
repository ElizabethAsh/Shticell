package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.Constants;
import engine.api.Engine;
import impl.engine.CellDTO;
import impl.engine.SheetDTO;
import impl.ui.CellIdDTO;
import impl.ui.CellUpdateRequestDTO;
import impl.ui.SheetUserIdentifierDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name = "DynamicAnalysisServlet", urlPatterns = {"/dynamic-analysis"})
public class DynamicAnalysisServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        String cellId = request.getParameter(Constants.CELL_ID);
        if (cellId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_MISSING_CELL_ID);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            CellDTO cellDTO = engine.isCellOriginalValueNumeric(new CellIdDTO(cellId), new SheetUserIdentifierDTO(sheetName, username));
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, gson.toJson(cellDTO));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null || username == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        CellUpdateRequestDTO updateRequest = gson.fromJson(request.getReader(), CellUpdateRequestDTO.class);

        if (updateRequest == null || updateRequest.getCoordinate() == null || updateRequest.getNewCellValue() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_OR_USER_SELECTED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            SheetDTO updatedSheet = engine.updateCellValueInDynamicAnalysis(updateRequest, new SheetUserIdentifierDTO(sheetName, username));
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, gson.toJson(updatedSheet));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, e.getMessage());
        }
    }

    private void writeResponse(HttpServletResponse response, String message) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
            out.flush();
        }
    }
}
