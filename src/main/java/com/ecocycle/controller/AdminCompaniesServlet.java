package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/companies")
public class AdminCompaniesServlet extends HttpServlet {

    private static final List<String> FILTERS =
            List.of("PENDING", "APPROVED", "REJECTED", "BLOCKED", "ALL");

    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filter = filterOf(req.getParameter("status"));
        try {
            req.setAttribute("companies",
                    "ALL".equals(filter) ? companyDAO.findAll() : companyDAO.findByStatus(filter));
        } catch (SQLException e) {
            throw new ServletException("Could not load companies", e);
        }
        req.setAttribute("filter", filter);
        req.getRequestDispatcher("/WEB-INF/views/admin/companies.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer companyId = Validator.parseInt(req.getParameter("companyId"), 1, Integer.MAX_VALUE);
        String action = req.getParameter("action");
        String filter = filterOf(req.getParameter("status"));

        // each action is allowed only from one particular current status
        String expected;
        String next;
        if ("approve".equals(action)) {
            expected = "PENDING";
            next = "APPROVED";
        } else if ("reject".equals(action)) {
            expected = "PENDING";
            next = "REJECTED";
        } else if ("block".equals(action)) {
            expected = "APPROVED";
            next = "BLOCKED";
        } else if ("unblock".equals(action)) {
            expected = "BLOCKED";
            next = "APPROVED";
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (companyId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean changed = companyDAO.updateStatusIfCurrent(companyId, expected, next);
            // redirect after POST, so refreshing does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/admin/companies?status=" + filter
                    + "&msg=" + (changed ? action : "stale"));
        } catch (SQLException e) {
            throw new ServletException("Could not update the company", e);
        }
    }

    /** Only values from the fixed list are accepted; anything else means PENDING. */
    private static String filterOf(String s) {
        return (s != null && FILTERS.contains(s)) ? s : "PENDING";
    }
}