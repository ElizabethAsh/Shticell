package servlets.SheetRoomActionsServlets;

import com.google.gson.Gson;
import engine.api.Engine;
import impl.ui.SheetUserIdentifierDTO;
import impl.ui.UpdateCellStyleRequestDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Constants.*;

@WebServlet(name = "UpdateCellStyleServlet", urlPatterns = {"/updateCellStyle/*"})
public class UpdateCellStyleServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String username = SessionUtils.getUsername(request);
        String sheetName = SessionUtils.getSheetName(request);

        if (sheetName == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(ERROR_NO_SHEET_SELECTED);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || (!pathInfo.equals(PATH_BACKGROUND) && !pathInfo.equals(PATH_TEXT))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(ERROR_INVALID_STYLE_TYPE);
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        UpdateCellStyleRequestDTO updateRequest = gson.fromJson(request.getReader(), UpdateCellStyleRequestDTO.class);
        SheetUserIdentifierDTO sheetUserIdentifierDTO = new SheetUserIdentifierDTO(sheetName, username);

        try {
            if (pathInfo.equals(PATH_BACKGROUND)) {
                engine.updateCellBackground(updateRequest, sheetUserIdentifierDTO);
            } else {
                engine.updateCellTextColor(updateRequest, sheetUserIdentifierDTO);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
        }
    }
}
