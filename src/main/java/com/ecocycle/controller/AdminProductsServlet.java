package com.ecocycle.controller;

import com.ecocycle.dao.ProductDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/products")
public class AdminProductsServlet extends HttpServlet {

    private static final List<String> FILTERS = List.of("ACTIVE", "REMOVED", "ALL");

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filter = filterOf(req.getParameter("status"));
        try {
            req.setAttribute("products",
                    productDAO.findForAdmin("ALL".equals(filter) ? null : filter, req.getParameter("q")));
        } catch (SQLException e) {
            throw new ServletException("Could not load products", e);
        }
        req.setAttribute("filter", filter);
        req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer productId = Validator.parseInt(req.getParameter("productId"), 1, Integer.MAX_VALUE);
        if (productId == null || !"remove".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String filter = filterOf(req.getParameter("status"));
        String q = req.getParameter("q");
        q = (q == null || q.length() > 100) ? "" : q.trim();

        try {
            boolean removed = productDAO.adminRemove(productId);
            // redirect after POST, keeping the tab and search the admin was looking at
            resp.sendRedirect(req.getContextPath() + "/admin/products?status=" + filter
                    + "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
                    + "&msg=" + (removed ? "removed" : "stale"));
        } catch (SQLException e) {
            throw new ServletException("Could not remove the product", e);
        }
    }

    /** Only values from the fixed list are accepted; anything else means ACTIVE. */
    private static String filterOf(String s) {
        return (s != null && FILTERS.contains(s)) ? s : "ACTIVE";
    }
}