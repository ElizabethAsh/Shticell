package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.api.Engine;
import impl.engine.RangeDTO;
import impl.engine.SheetDTO;
import impl.ui.AddNewRangeRequestDTO;
import impl.ui.RangeNameDTO;
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

@WebServlet(name = "RangeServlet", urlPatterns = {"/range"})
public class RangeServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (sheetName == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, ERROR_NO_SHEET_SELECTED);
            return;
        }

        AddNewRangeRequestDTO addNewRangeRequestDTO = gson.fromJson(request.getReader(), AddNewRangeRequestDTO.class);
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            SheetDTO updatedSheet = engine.addNewRange(addNewRangeRequestDTO, sheetUserIdentifierDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.println(gson.toJson(updatedSheet));
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (sheetName == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, ERROR_NO_SHEET_SELECTED);
            return;
        }

        RangeNameDTO rangeNameDTO = gson.fromJson(request.getReader(), RangeNameDTO.class);
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            engine.deleteRange(rangeNameDTO, sheetUserIdentifierDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(SUCCESS_RANGE_DELETED);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (sheetName == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, ERROR_NO_SHEET_SELECTED);
            return;
        }

        String rangeName = request.getParameter(RANGE_NAME);
        if (rangeName == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, ERROR_NO_RANGE_NAME_PROVIDED);
            return;
        }

        RangeNameDTO rangeNameDTO = new RangeNameDTO(rangeName);
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            RangeDTO rangeDTO = engine.displayRange(rangeNameDTO, sheetUserIdentifierDTO);
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()) {
                out.println(gson.toJson(rangeDTO));
            }
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
        }
    }
}
