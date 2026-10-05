package com.ecocycle.controller;

import com.ecocycle.dao.PaymentDAO;
import com.ecocycle.model.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/transactions")
public class UserTransactionsServlet extends HttpServlet {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int userId = (Integer) req.getSession().getAttribute("userId");
        try {
            List<Payment> payments = paymentDAO.findByUser(userId);

            BigDecimal total = BigDecimal.ZERO;
            for (Payment p : payments) {
                total = total.add(p.getAmount());
            }

            req.setAttribute("payments", payments);
            req.setAttribute("totalReceived", total);
        } catch (SQLException e) {
            throw new ServletException("Could not load transactions", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/transactions.jsp").forward(req, resp);
    }
}