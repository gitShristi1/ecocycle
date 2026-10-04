package com.ecocycle.controller;

import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.User;
import com.ecocycle.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userDAO.findByEmail(email);

            // same message for "no such email" and "wrong password"
            if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
                showError(req, resp, "Invalid email or password.");
                return;
            }
            if ("BLOCKED".equals(user.getStatus())) {
                showError(req, resp, "Your account has been blocked. Please contact the admin.");
                return;
            }
            
            req.getSession();
            req.changeSessionId();   // new session id after login (prevents session fixation)
            HttpSession session = req.getSession();
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("role", "USER");

            resp.sendRedirect(req.getContextPath() + "/user/dashboard");
        } catch (SQLException e) {
            getServletContext().log("Login failed", e);
            showError(req, resp, "Something went wrong. Please try again.");
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}