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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");

        int requestId;
        try {
            requestId = Integer.parseInt(req.getParameter("requestId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!"cancel".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean cancelled = requestDAO.cancelIfSubmitted(requestId, userId);
            String msg = cancelled ? "cancelled" : "cannotcancel";
            // redirect after POST, so refreshing does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/user/requests?msg=" + msg);
        } catch (SQLException e) {
            throw new ServletException("Could not cancel request", e);
        }
    }
}