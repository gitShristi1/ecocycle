package com.ecocycle.controller;

import com.ecocycle.dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/orders")
public class UserOrdersServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        try {
            req.setAttribute("orders", orderDAO.findByUser(userId));
        } catch (SQLException e) {
            throw new ServletException("Could not load orders", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/orders.jsp").forward(req, resp);
    }
}