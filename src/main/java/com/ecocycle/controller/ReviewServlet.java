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

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/review")
public class ReviewServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (Integer) req.getSession().getAttribute("userId");

        Integer productId = Validator.parseInt(req.getParameter("productId"), 1, Integer.MAX_VALUE);
        if (productId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Integer rating = Validator.parseInt(req.getParameter("rating"), 1, 5);
        String text = req.getParameter("reviewText");
        text = (text == null || text.trim().isEmpty()) ? null : text.trim();

        String msg;
        if (rating == null || Validator.tooLong(text, 1000)) {
            msg = "invalid";
        } else {
            try {
                msg = feedbackDAO.save(userId, productId, rating, text) ? "reviewed" : "notbuyer";
            } catch (SQLException e) {
                throw new ServletException("Could not save the review", e);
            }
        }

        // redirect after POST, so refreshing does not repeat the action
        resp.sendRedirect(req.getContextPath() + "/user/product?id=" + productId + "&msg=" + msg);
    }
}