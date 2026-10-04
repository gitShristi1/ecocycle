package com.ecocycle.controller;

import com.ecocycle.dao.AdminDAO;
import com.ecocycle.model.Admin;
import com.ecocycle.util.PasswordUtil;
import com.ecocycle.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Admin admin = adminDAO.findByEmail(email);

            if (admin == null || !PasswordUtil.matches(password, admin.getPasswordHash())) {
                showError(req, resp, "Invalid email or password.");
                return;
            }

            HttpSession session = SessionUtil.startSession(req, "ADMIN");
            session.setAttribute("adminId", admin.getAdminId());
            session.setAttribute("adminName", admin.getFullName());
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            getServletContext().log("Admin login failed", e);
            showError(req, resp, "Something went wrong. Please try again.");
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/admin-login.jsp").forward(req, resp);
    }
}