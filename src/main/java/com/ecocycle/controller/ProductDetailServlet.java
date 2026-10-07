package com.ecocycle.controller;

import com.ecocycle.dao.FeedbackDAO;
import com.ecocycle.dao.ProductDAO;
import com.ecocycle.model.Product;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/product")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        Integer id = Validator.parseInt(req.getParameter("id"), 1, Integer.MAX_VALUE);
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            // only products that are visible in the store
            List<Product> found = productDAO.findStoreProducts(List.of(id));
            if (found.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("product", found.get(0));
            req.setAttribute("reviews", feedbackDAO.findByProduct(id));
            req.setAttribute("rating", feedbackDAO.getProductRating(id));
            req.setAttribute("canReview", feedbackDAO.hasPurchased(userId, id));
            req.setAttribute("myReview", feedbackDAO.findByUserAndProduct(userId, id));
        } catch (SQLException e) {
            throw new ServletException("Could not load the product", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/product.jsp").forward(req, resp);
    }
}