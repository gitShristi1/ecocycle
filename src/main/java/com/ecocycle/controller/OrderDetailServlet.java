package com.ecocycle.controller;

import com.ecocycle.dao.OrderDAO;
import com.ecocycle.model.Order;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/order")
public class OrderDetailServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        Integer orderId = Validator.parseInt(req.getParameter("id"), 1, Integer.MAX_VALUE);

        if (orderId == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Order order = orderDAO.findByIdForUser(orderId, userId);
            if (order == null) {
                // someone else's order looks exactly like one that does not exist
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("order", order);
            req.setAttribute("items", orderDAO.findItems(orderId));
        } catch (SQLException e) {
            throw new ServletException("Could not load the order", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/order-detail.jsp").forward(req, resp);
    }
}