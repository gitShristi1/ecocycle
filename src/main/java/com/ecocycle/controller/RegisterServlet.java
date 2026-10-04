package com.ecocycle.controller;

import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.User;
import com.ecocycle.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isBlank(fullName) || isBlank(email) || isBlank(password)) {
            showError(req, resp, "Name, email and password are required.");
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                showError(req, resp, "An account with this email already exists.");
                return;
            }

            User u = new User();
            u.setFullName(fullName.trim());
            u.setEmail(email);
            u.setPasswordHash(PasswordUtil.hash(password));
            u.setPhone(req.getParameter("phone"));
            u.setAddress(req.getParameter("address"));
            u.setCity(req.getParameter("city"));
            userDAO.insert(u);

            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (SQLException e) {
            getServletContext().log("Registration failed", e);
            showError(req, resp, "Something went wrong. Please try again.");
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}