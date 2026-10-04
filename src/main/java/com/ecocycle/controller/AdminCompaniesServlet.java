package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/companies")
public class AdminCompaniesServlet extends HttpServlet {

    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("pending", companyDAO.findByStatus("PENDING"));
        } catch (SQLException e) {
            throw new ServletException("Could not load pending companies", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/companies.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        int companyId;
        try {
            companyId = Integer.parseInt(req.getParameter("companyId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String newStatus;
        if ("approve".equals(action)) {
            newStatus = "APPROVED";
        } else if ("reject".equals(action)) {
            newStatus = "REJECTED";
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            // only a PENDING company can be approved or rejected here
            boolean changed = companyDAO.updateStatusIfCurrent(companyId, "PENDING", newStatus);
            String msg = changed ? action : "stale";
            // redirect after POST, so refreshing the page does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/admin/companies?msg=" + msg);
        } catch (SQLException e) {
            throw new ServletException("Could not update company status", e);
        }
    }
}