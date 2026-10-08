package com.ecocycle.controller;

import com.ecocycle.dao.FeedbackDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/feedback")
public class AdminFeedbackServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("reviews", feedbackDAO.findAllForAdmin());
        } catch (SQLException e) {
            throw new ServletException("Could not load feedback", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/feedback.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = Validator.parseInt(req.getParameter("feedbackId"), 1, Integer.MAX_VALUE);
        if (id == null || !"delete".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean deleted = feedbackDAO.delete(id);
            // redirect after POST, so refreshing does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/admin/feedback?msg=" + (deleted ? "deleted" : "stale"));
        } catch (SQLException e) {
            throw new ServletException("Could not delete the review", e);
        }
    }
}