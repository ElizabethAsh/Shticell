package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.api.Engine;
import impl.engine.AvailableColumnsForSortDTO;
import impl.engine.SheetDTO;
import impl.ui.RangeBoundariesForSortingOrFilterDTO;
import impl.ui.SheetUserIdentifierDTO;
import impl.ui.SortRequestDTO;
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

@WebServlet(name = "SortServlet", urlPatterns = {"/sort"})
public class SortServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        String from = request.getParameter(FROM_COORDINATE);
        String to = request.getParameter(TO_COORDINATE);

        if (from == null || to == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_MISSING_PARAMETERS);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            AvailableColumnsForSortDTO availableColumns = engine.getAvailableColumnsForSort(
                    new RangeBoundariesForSortingOrFilterDTO(from, to), sheetUserIdentifierDTO
            );
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, gson.toJson(availableColumns));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, ERROR_NO_SHEET_SELECTED);
            return;
        }

        SortRequestDTO sortRequestDTO = gson.fromJson(request.getReader(), SortRequestDTO.class);
        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            SheetDTO sortedSheet = engine.sortSheet(sortRequestDTO, sheetUserIdentifierDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, gson.toJson(sortedSheet));

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
