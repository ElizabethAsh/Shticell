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

import static constants.Constants.ERROR_MISSING_CELL_ID;
import static constants.Constants.ERROR_NO_SHEET_SELECTED;

@WebServlet(name = "CellServlet", urlPatterns = {"/cell"})
public class CellServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String sheetName = SessionUtils.getSheetName(request);
        String username = SessionUtils.getUsername(request);
        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        String cellId = request.getParameter(Constants.CELL_ID);
        if (cellId == null || cellId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_MISSING_CELL_ID);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            CellDTO cellDTO = engine.displayCell(new CellIdDTO(cellId), sheetUserIdentifierDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, gson.toJson(cellDTO));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeResponse(response, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        CellUpdateRequestDTO updateRequest = gson.fromJson(request.getReader(), CellUpdateRequestDTO.class);
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            SheetDTO updatedSheet = engine.updateCellValue(updateRequest, sheetUserIdentifierDTO);
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
        }
    }
}
