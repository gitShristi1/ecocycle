package com.ecocycle.controller;

import com.ecocycle.dao.WasteRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/requests")
public class AdminRequestsServlet extends HttpServlet {

    private static final List<String> FILTERS =
            List.of("ALL", "SUBMITTED", "ACCEPTED", "PICKED_UP", "PAID", "CANCELLED");

    private final WasteRequestDAO requestDAO = new WasteRequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // only values from the fixed list are accepted; anything else means ALL
        String filter = req.getParameter("status");
        if (filter == null || !FILTERS.contains(filter)) {
            filter = "ALL";
        }

        try {
            req.setAttribute("requests", requestDAO.findForAdmin("ALL".equals(filter) ? null : filter));
        } catch (SQLException e) {
            throw new ServletException("Could not load waste requests", e);
        }
        req.setAttribute("filter", filter);
        req.getRequestDispatcher("/WEB-INF/views/admin/requests.jsp").forward(req, resp);
    }
}