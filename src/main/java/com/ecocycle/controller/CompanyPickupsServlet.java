package com.ecocycle.controller;

import com.ecocycle.dao.PaymentDAO;
import com.ecocycle.dao.WasteRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/pickups")
public class CompanyPickupsServlet extends HttpServlet {

    private final WasteRequestDAO requestDAO = new WasteRequestDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        try {
            req.setAttribute("requests", requestDAO.findByCompany(companyId));
        } catch (SQLException e) {
            throw new ServletException("Could not load pickups", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/company/pickups.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");

        int requestId;
        try {
            requestId = Integer.parseInt(req.getParameter("requestId"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String action = req.getParameter("action");
        String msg;
        try {
            if ("pickedup".equals(action)) {
                msg = requestDAO.markPickedUp(requestId, companyId) ? "pickedup" : "invalid";
            } else if ("pay".equals(action)) {
                msg = paymentDAO.recordPayment(requestId, companyId) ? "paid" : "invalid";
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (SQLException e) {
            getServletContext().log("Pickup action failed: " + action, e);
            msg = "error";
        }

        // redirect after POST, so refreshing does not repeat the action
        resp.sendRedirect(req.getContextPath() + "/company/pickups?msg=" + msg);
    }
}