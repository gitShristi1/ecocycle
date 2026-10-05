package com.ecocycle.controller;

import com.ecocycle.dao.WasteRequestDAO;
import com.ecocycle.dao.WasteTypeDAO;
import com.ecocycle.model.WasteRequest;
import com.ecocycle.model.WasteType;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/sell")
public class SellWasteServlet extends HttpServlet {

    private static final BigDecimal MIN_WEIGHT = new BigDecimal("0.01");
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("10000");

    private final WasteTypeDAO typeDAO = new WasteTypeDAO();
    private final WasteRequestDAO requestDAO = new WasteRequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        show(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        int typeId = parseId(req.getParameter("wasteTypeId"));
        BigDecimal weight = Validator.parseDecimal(req.getParameter("weightKg"), MIN_WEIGHT, MAX_WEIGHT, 2);
        String address = clean(req.getParameter("pickupAddress"));
        String city = clean(req.getParameter("city"));
        String notes = clean(req.getParameter("notes"));

        List<String> errors = new ArrayList<>();

        if (typeId < 0) {
            errors.add("Choose a waste type.");
        }
        if (weight == null) {
            errors.add("Weight must be between 0.01 and 10000 kg, with at most 2 decimals.");
        }
        if (Validator.isBlank(address)) {
            errors.add("Pickup address is required.");
        } else if (Validator.tooLong(address, 300)) {
            errors.add("Pickup address must be at most 300 characters.");
        }
        if (Validator.tooLong(city, 80)) {
            errors.add("City must be at most 80 characters.");
        }
        if (Validator.tooLong(notes, 500)) {
            errors.add("Notes must be at most 500 characters.");
        }

        try {
            WasteType type = null;
            if (typeId >= 0) {
                type = typeDAO.findById(typeId);
                if (type == null || !type.isActive()) {
                    errors.add("The selected waste type is not available.");
                }
            }
            if (!errors.isEmpty()) {
                show(req, resp, errors);
                return;
            }

            // the price is always calculated here from the current database rate
            BigDecimal total = weight.multiply(type.getRatePerKg()).setScale(2, RoundingMode.HALF_UP);

            WasteRequest r = new WasteRequest();
            r.setUserId((Integer) req.getSession().getAttribute("userId"));
            r.setWasteTypeId(type.getWasteTypeId());
            r.setWeightKg(weight);
            r.setRatePerKg(type.getRatePerKg());
            r.setTotalAmount(total);
            r.setPickupAddress(address);
            r.setCity(city);
            r.setNotes(notes);
            requestDAO.insert(r);

            resp.sendRedirect(req.getContextPath() + "/user/requests?msg=submitted");
        } catch (SQLException e) {
            getServletContext().log("Waste request failed", e);
            show(req, resp, List.of("Something went wrong. Please try again."));
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, List<String> errors)
            throws ServletException, IOException {
        try {
            req.setAttribute("wasteTypes", typeDAO.findActive());
        } catch (SQLException e) {
            throw new ServletException("Could not load waste types", e);
        }
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/WEB-INF/views/user/sell.jsp").forward(req, resp);
    }

    private static int parseId(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String clean(String s) {
        return s == null ? null : s.trim();
    }
}