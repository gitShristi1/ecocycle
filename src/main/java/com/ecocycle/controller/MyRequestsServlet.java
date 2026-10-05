package com.ecocycle.controller;

import com.ecocycle.dao.WasteRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/requests")
public class MyRequestsServlet extends HttpServlet {

    private final WasteRequestDAO requestDAO = new WasteRequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        try {
            req.setAttribute("requests", requestDAO.findByUser(userId));
        } catch (SQLException e) {
            throw new ServletException("Could not load requests", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/requests.jsp").forward(req, resp);
    }
}