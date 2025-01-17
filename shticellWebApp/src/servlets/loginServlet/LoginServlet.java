package servlets.loginServlet;

import engine.api.Engine;
import impl.ui.UserNameDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;

import static constants.Constants.USERNAME;

@WebServlet(name = "LightweightLoginServlet", urlPatterns = {"/loginShortResponse"})
public class LoginServlet extends HttpServlet {
    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) {

            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);

            } else {
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    UserNameDTO userNameDTO = new UserNameDTO(usernameFromParameter);
                    if (engine.isUserExists(userNameDTO)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        engine.addUser(userNameDTO);
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);

                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
