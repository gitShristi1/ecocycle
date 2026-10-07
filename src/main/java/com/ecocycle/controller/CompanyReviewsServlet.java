package com.ecocycle.controller;

import com.ecocycle.dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/reviews")
public class CompanyReviewsServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        try {
            req.setAttribute("rating", feedbackDAO.getCompanyRating(companyId));
            req.setAttribute("reviews", feedbackDAO.findByCompany(companyId));
        } catch (SQLException e) {
            throw new ServletException("Could not load reviews", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/company/reviews.jsp").forward(req, resp);
    }
}