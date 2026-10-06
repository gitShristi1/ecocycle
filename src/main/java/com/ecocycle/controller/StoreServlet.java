package com.ecocycle.controller;

import com.ecocycle.dao.ProductDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/store")
public class StoreServlet extends HttpServlet {

    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000");

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String q = req.getParameter("q");

        // an invalid price limit is simply ignored
        BigDecimal maxPrice = Validator.parseDecimal(
                req.getParameter("maxPrice"), BigDecimal.ZERO, MAX_PRICE, 2);

        boolean inStockOnly = "1".equals(req.getParameter("inStock"));
        String sort = req.getParameter("sort");

        try {
            req.setAttribute("products", productDAO.search(q, maxPrice, inStockOnly, sort));
        } catch (SQLException e) {
            throw new ServletException("Could not load products", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/store.jsp").forward(req, resp);
    }
}