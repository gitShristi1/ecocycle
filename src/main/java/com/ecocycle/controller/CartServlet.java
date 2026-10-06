package com.ecocycle.controller;

import com.ecocycle.dao.ProductDAO;
import com.ecocycle.model.Cart;
import com.ecocycle.model.CartLine;
import com.ecocycle.model.Product;
import com.ecocycle.service.CartService;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Access is checked by AuthFilter (role USER).
@WebServlet("/user/cart")
public class CartServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Cart cart = Cart.of(session);

        // a message left by the checkout page (shown once)
        String cartError = (String) session.getAttribute("cartError");
        if (cartError != null) {
            req.setAttribute("cartError", cartError);
            session.removeAttribute("cartError");
        }

        try {
            int before = cart.getItems().size();
            List<CartLine> lines = CartService.loadLines(cart);

            req.setAttribute("lines", lines);
            req.setAttribute("total", CartService.total(lines));
            req.setAttribute("blocked", CartService.hasProblem(lines));
            req.setAttribute("removedCount", before - lines.size());
        } catch (SQLException e) {
            throw new ServletException("Could not load the cart", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/user/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Cart cart = Cart.of(req.getSession());
        String action = req.getParameter("action");
        Integer productId = Validator.parseInt(req.getParameter("productId"), 1, Integer.MAX_VALUE);

        if (productId == null || action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String msg;
        try {
            if ("add".equals(action)) {
                msg = add(cart, productId, req.getParameter("quantity"));
            } else if ("update".equals(action)) {
                msg = update(cart, productId, req.getParameter("quantity"));
            } else if ("remove".equals(action)) {
                cart.remove(productId);
                msg = "removed";
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Cart action failed", e);
        }

        // redirect after POST, so refreshing does not repeat the action
        resp.sendRedirect(req.getContextPath() + "/user/cart?msg=" + msg);
    }

    private String add(Cart cart, int productId, String quantityText) throws SQLException {
        Integer qty = Validator.parseInt(quantityText, 1, Cart.MAX_QUANTITY);
        if (qty == null) {
            return "invalid";
        }

        List<Product> found = productDAO.findStoreProducts(List.of(productId));
        if (found.isEmpty()) {
            return "unavailable";
        }
        Product p = found.get(0);
        if (p.getStock() <= 0) {
            return "outofstock";
        }
        if (!cart.canAdd(productId)) {
            return "cartfull";
        }

        int wanted = cart.getQuantity(productId) + qty;
        int limit = Math.min(p.getStock(), Cart.MAX_QUANTITY);
        int capped = Math.min(wanted, limit);
        cart.setQuantity(productId, capped);
        return capped < wanted ? "limited" : "added";
    }

    private String update(Cart cart, int productId, String quantityText) throws SQLException {
        Integer qty = Validator.parseInt(quantityText, 0, Cart.MAX_QUANTITY);
        if (qty == null || cart.getQuantity(productId) == 0) {
            return "invalid";               // bad number, or not a product in this cart
        }
        if (qty == 0) {
            cart.remove(productId);
            return "removed";
        }

        List<Product> found = productDAO.findStoreProducts(List.of(productId));
        if (found.isEmpty()) {
            cart.remove(productId);
            return "unavailable";
        }
        int stock = found.get(0).getStock();
        if (stock <= 0) {
            cart.remove(productId);
            return "outofstock";
        }
        int capped = Math.min(qty, stock);
        cart.setQuantity(productId, capped);
        return capped < qty ? "limited" : "updated";
    }
}