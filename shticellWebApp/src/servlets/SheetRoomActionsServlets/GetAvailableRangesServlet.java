package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import engine.api.Engine;
import impl.ui.RangeNameDTO;
import impl.ui.SheetUserIdentifierDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static constants.Constants.ERROR_NO_SHEET_SELECTED;

public class GetAvailableRangesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);
        String action = request.getParameter("action");

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.println(ERROR_NO_SHEET_SELECTED);
            }
            return;
        }

        if (action == null || (!action.equals("delete") && !action.equals("display"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.println("Error: Invalid action. Supported actions are 'delete' or 'display'.");
            }
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);
        try {
            Set<RangeNameDTO> ranges;
            if (action.equals("delete")) {
                ranges = engine.getRangesForDeleteRequest(sheetUserIdentifierDTO);
            } else {
                ranges = engine.getRangesForDisplayRequest(sheetUserIdentifierDTO);
            }

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(ranges);

            try (PrintWriter out = response.getWriter()) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.println(jsonResponse);
                out.flush();
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println(e.getMessage());
                out.flush();
            }
        }
    }
}
