package com.ecocycle.controller;

import com.ecocycle.dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/products")
public class CompanyProductsServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        try {
            req.setAttribute("products", productDAO.findByCompany(companyId));
        } catch (SQLException e) {
            throw new ServletException("Could not load products", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/company/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");

        int productId;
        try {
            productId = Integer.parseInt(req.getParameter("productId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!"remove".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            String msg = productDAO.remove(productId, companyId) ? "removed" : "invalid";
            // redirect after POST, so refreshing does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/company/products?msg=" + msg);
        } catch (SQLException e) {
            throw new ServletException("Could not remove product", e);
        }
    }
}