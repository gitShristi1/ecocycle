package com.ecocycle.controller;

import com.ecocycle.dao.SettingsDAO;
import com.ecocycle.dao.WasteTypeDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/pricing")
public class AdminPricingServlet extends HttpServlet {

    private static final BigDecimal MAX_RATE = new BigDecimal("10000");

    private final WasteTypeDAO typeDAO = new WasteTypeDAO();
    
        private static final BigDecimal MAX_COMMISSION = new BigDecimal("50");

    private final SettingsDAO settingsDAO = new SettingsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("types", typeDAO.findAll());
            req.setAttribute("commission", settingsDAO.getCommissionPercent());
        } catch (SQLException e) {
            throw new ServletException("Could not load waste types", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/pricing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String msg;

        try {
            if ("add".equals(action)) {
                msg = addType(req);
            } else if ("rate".equals(action)) {
                msg = updateRate(req);
            } else if ("toggle".equals(action)) {
                msg = toggle(req);
            } else if ("commission".equals(action)) {
                msg = updateCommission(req);
            }else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {          // ORA-00001: duplicate type name
                msg = "exists";
            } else {
                throw new ServletException("Pricing update failed", e);
            }
        }

        // redirect after POST, so refreshing does not repeat the action
        resp.sendRedirect(req.getContextPath() + "/admin/pricing?msg=" + msg);
    }

    private String addType(HttpServletRequest req) throws SQLException {
        String name = req.getParameter("typeName");
        name = (name == null) ? null : name.trim();
        BigDecimal rate = Validator.parseDecimal(req.getParameter("rate"), BigDecimal.ZERO, MAX_RATE, 2);

        if (Validator.isBlank(name) || Validator.tooLong(name, 50) || rate == null) {
            return "invalid";
        }
        typeDAO.insert(name, rate);
        return "added";
    }

    private String updateRate(HttpServletRequest req) throws SQLException {
        int id = parseId(req.getParameter("id"));
        BigDecimal rate = Validator.parseDecimal(req.getParameter("rate"), BigDecimal.ZERO, MAX_RATE, 2);

        if (id < 0 || rate == null) {
            return "invalid";
        }
        return typeDAO.updateRate(id, rate) ? "updated" : "invalid";
    }

    private String toggle(HttpServletRequest req) throws SQLException {
        int id = parseId(req.getParameter("id"));
        String active = req.getParameter("active");

        if (id < 0 || !("Y".equals(active) || "N".equals(active))) {
            return "invalid";
        }
        return typeDAO.setActive(id, "Y".equals(active)) ? "toggled" : "invalid";
    }
    
    private String updateCommission(HttpServletRequest req) throws SQLException {
        BigDecimal percent = Validator.parseDecimal(
                req.getParameter("percent"), BigDecimal.ZERO, MAX_COMMISSION, 2);
        if (percent == null) {
            return "badcommission";
        }
        settingsDAO.setCommissionPercent(percent);
        return "commission";
    }

    private static int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}