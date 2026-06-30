package com.example.erp.servlet;

import com.example.erp.dao.UserLoginDAO;
import com.example.erp.model.UserLogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet controller handling Login authentication, Input Validation, 
 * Session Management, and Role-based view routing.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserLoginDAO loginDAO;

    @Override
    public void init() throws ServletException {
        this.loginDAO = new UserLoginDAO();
    }

    /**
     * Renders the login page view.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Direct to login JSP page
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /**
     * Processes login credentials submissions, performs server-side validation, 
     * manages session states, and redirects authenticated requests.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Retrieve request attributes
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // 2. Server-side validation
        String errorMessage = null;
        if (username == null || username.trim().isEmpty()) {
            errorMessage = "Username or Portal ID is required.";
        } else if (password == null || password.trim().isEmpty()) {
            errorMessage = "Password is required.";
        } else if (password.trim().length() < 6) {
            errorMessage = "Password must be at least 6 characters long.";
        }

        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        // 3. Database Authentication lookup
        UserLogin authenticatedUser = loginDAO.authenticate(username.trim(), password);

        if (authenticatedUser != null) {
            // 4. Secure Session Management
            HttpSession session = request.getSession(true); // Create new session or fetch existing
            session.setAttribute("user", authenticatedUser);
            session.setAttribute("userId", authenticatedUser.getId());
            session.setAttribute("username", authenticatedUser.getUsername());
            session.setAttribute("userRole", authenticatedUser.getRole());
            
            // Set session timeout limit (e.g., 30 minutes of inactivity)
            session.setMaxInactiveInterval(30 * 60);

            // 5. Role-based Navigation Routing
            switch (authenticatedUser.getRole()) {
                case "ADMIN":
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    break;
                case "STAFF":
                    response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                    break;
                case "STUDENT":
                    response.sendRedirect(request.getContextPath() + "/student/dashboard");
                    break;
                default:
                    // Fallback
                    response.sendRedirect(request.getContextPath() + "/login?error=InvalidRole");
                    break;
            }
        } else {
            // Access Denied
            request.setAttribute("error", "Access Denied: Invalid credentials or deactivated account.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
