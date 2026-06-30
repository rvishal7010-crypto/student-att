package com.example.erp.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling secure logout operations, invalidating sessions, 
 * and redirecting the user back to the login portal.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Fetch session if exists
        if (session != null) {
            session.removeAttribute("user");
            session.removeAttribute("userId");
            session.removeAttribute("username");
            session.removeAttribute("userRole");
            session.invalidate(); // De-allocate and destroy session
        }
        
        // Redirect to login screen with logged out indicator
        response.sendRedirect(request.getContextPath() + "/login?msg=logged_out");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
