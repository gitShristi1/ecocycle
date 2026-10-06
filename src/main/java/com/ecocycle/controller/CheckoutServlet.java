package com.ecocycle.controller;

import com.ecocycle.dao.OrderDAO;
import com.ecocycle.dao.OrderException;
import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.Cart;
import com.ecocycle.model.CartLine;
import com.ecocycle.model.User;
import com.ecocycle.service.CartService;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Cart cart = Cart.of(req.getSession());
        try {
            List<CartLine> lines = CartService.loadLines(cart);
            if (lines.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/user/cart");
                return;
            }
            if (CartService.hasProblem(lines)) {
                resp.sendRedirect(req.getContextPath() + "/user/cart?msg=review");
                return;
            }

            // start from the address saved in the profile
            int userId = (Integer) req.getSession().getAttribute("userId");
            User profile = userDAO.findById(userId);
            String address = "";
            if (profile != null) {
                address = join(profile.getAddress(), profile.getCity());
            }
            show(req, resp, lines, null, address);
        } catch (SQLException e) {
            throw new ServletException("Could not load checkout", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int userId = (Integer) req.getSession().getAttribute("userId");
        Cart cart = Cart.of(req.getSession());
        String address = req.getParameter("shippingAddress");
        address = (address == null) ? "" : address.trim();

        // one order at a time per session: a double-click cannot order twice
        synchronized (cart) {
            try {
                List<CartLine> lines = CartService.loadLines(cart);
                if (lines.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/user/cart");
                    return;
                }
                if (CartService.hasProblem(lines)) {
                    resp.sendRedirect(req.getContextPath() + "/user/cart?msg=review");
                    return;
                }

                List<String> errors = new ArrayList<>();
                if (Validator.isBlank(address)) {
                    errors.add("Shipping address is required.");
                } else if (Validator.tooLong(address, 300)) {
                    errors.add("Shipping address must be at most 300 characters.");
                }
                if (!errors.isEmpty()) {
                    show(req, resp, lines, errors, address);
                    return;
                }

                try {
                    int orderId = orderDAO.placeOrder(userId, address, cart.getItems());
                    cart.clear();
                    resp.sendRedirect(req.getContextPath() + "/user/orders?msg=placed&id=" + orderId);
                } catch (OrderException e) {
                    // sold out or gone: nothing was saved, tell the customer on the cart page
                    req.getSession().setAttribute("cartError", e.getMessage());
                    resp.sendRedirect(req.getContextPath() + "/user/cart?msg=failed");
                }
            } catch (SQLException e) {
                getServletContext().log("Checkout failed", e);
                try {
                    show(req, resp, CartService.loadLines(cart),
                            List.of("Something went wrong and nothing was ordered. Please try again."),
                            address);
                } catch (SQLException e2) {
                    throw new ServletException("Checkout failed", e2);
                }
            }
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, List<CartLine> lines,
                      List<String> errors, String address) throws ServletException, IOException {
        req.setAttribute("lines", lines);
        req.setAttribute("total", CartService.total(lines));
        req.setAttribute("errors", errors);
        req.setAttribute("shippingAddress", address);
        req.getRequestDispatcher("/WEB-INF/views/user/checkout.jsp").forward(req, resp);
    }

    private static String join(String address, String city) {
        boolean hasAddress = address != null && !address.trim().isEmpty();
        boolean hasCity = city != null && !city.trim().isEmpty();
        if (hasAddress && hasCity) {
            return address.trim() + ", " + city.trim();
        }
        return hasAddress ? address.trim() : (hasCity ? city.trim() : "");
    }
}