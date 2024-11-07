package servlets.dashBoardServlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.api.Engine;
import impl.engine.SheetDTO;
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

import static constants.Constants.CURRENT_SHEET_NAME;

@WebServlet(name = "ViewSheetServlet", urlPatterns = {"/view/sheet"})
public class ViewSheetServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            String usernameFromSession = SessionUtils.getUsername(request);
            String sheetName = request.getParameter(CURRENT_SHEET_NAME);

            if (sheetName != null && !sheetName.isEmpty()) {
                request.getSession().setAttribute(CURRENT_SHEET_NAME, sheetName);
            } else {
                sheetName = SessionUtils.getSheetName(request);
                if (sheetName == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("Error: No sheet is selected. Please select a sheet to view first.");
                    return;
                }
            }

            Engine engine = ServletUtils.getEngine(getServletContext());
            try {
                SheetUserIdentifierDTO dataToEngine = new SheetUserIdentifierDTO(sheetName, usernameFromSession);
                SheetDTO sheetDTO = engine.getSheetForView(dataToEngine);
                String json = gson.toJson(sheetDTO);
                out.println(json);
                out.flush();
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println(e.getMessage());
            }
        }
    }
}
