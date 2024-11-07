package servlets.dashBoardServlets;

import com.google.gson.Gson;
import engine.api.Engine;
import impl.engine.SheetDetailsDTO;
import impl.ui.UserNameDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "NewSheetsInfoServlet", urlPatterns = {"/availableSheets/update"})
public class AvailableSheetsServlet extends HttpServlet {

    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.println("error:User not logged in.");
                out.flush();
            }
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();

            Engine engine = ServletUtils.getEngine(getServletContext());
            UserNameDTO userNameDTO = new UserNameDTO(usernameFromSession);
            List<SheetDetailsDTO> sheetLines;

            sheetLines = engine.getSheetsInfo(userNameDTO);

            String json = gson.toJson(sheetLines);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: Unable to fetch sheets list.");
        }
    }
}
