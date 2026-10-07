package com.ecocycle.controller;

import com.ecocycle.dao.SalesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/sales")
public class CompanySalesServlet extends HttpServlet {

    private final SalesDAO salesDAO = new SalesDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        try {
            req.setAttribute("summary", salesDAO.getSummary(companyId));
            req.setAttribute("byProduct", salesDAO.findByProduct(companyId));
            req.setAttribute("recent", salesDAO.findRecent(companyId, 10));
        } catch (SQLException e) {
            throw new ServletException("Could not load sales", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/company/sales.jsp").forward(req, resp);
    }
}