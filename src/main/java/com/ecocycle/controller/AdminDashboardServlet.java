package com.ecocycle.controller;

import com.ecocycle.dao.StatsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final StatsDAO statsDAO = new StatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("stats", statsDAO.load());
        } catch (SQLException e) {
            // the dashboard still opens, it just shows no numbers
            getServletContext().log("Could not load platform statistics", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}