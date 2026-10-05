package com.ecocycle.controller;

import com.ecocycle.dao.WasteRequestDAO;
import com.ecocycle.dao.WasteTypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/requests")
public class CompanyRequestsServlet extends HttpServlet {

    private final WasteRequestDAO requestDAO = new WasteRequestDAO();
    private final WasteTypeDAO typeDAO = new WasteTypeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int typeId = parseId(req.getParameter("typeId"));
        String city = req.getParameter("city");

        try {
            req.setAttribute("requests", requestDAO.findOpen(typeId, city));
            req.setAttribute("wasteTypes", typeDAO.findAll());
        } catch (SQLException e) {
            throw new ServletException("Could not load open requests", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/company/requests.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        int requestId = parseId(req.getParameter("requestId"));

        if (requestId < 0 || !"accept".equals(req.getParameter("action"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            boolean accepted = requestDAO.accept(requestId, companyId);
            String msg = accepted ? "accepted" : "taken";
            // redirect after POST, so refreshing does not repeat the action
            resp.sendRedirect(req.getContextPath() + "/company/requests?msg=" + msg);
        } catch (SQLException e) {
            throw new ServletException("Could not accept request", e);
        }
    }

    private static int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}