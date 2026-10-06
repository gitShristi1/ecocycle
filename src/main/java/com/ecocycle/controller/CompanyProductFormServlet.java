package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.dao.ProductDAO;
import com.ecocycle.model.Company;
import com.ecocycle.model.Product;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/product-form")
public class CompanyProductFormServlet extends HttpServlet {

    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000");

    private final ProductDAO productDAO = new ProductDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int companyId = (Integer) req.getSession().getAttribute("companyId");
        String idParam = req.getParameter("id");

        if (idParam != null && !idParam.trim().isEmpty()) {
            int id = parseId(idParam);
            try {
                Product p = (id < 0) ? null : productDAO.findOwnedActive(id, companyId);
                if (p == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                req.setAttribute("editId", p.getProductId());
                fill(req, p.getProductName(), p.getDescription(),
                        p.getPrice().toPlainString(), String.valueOf(p.getStock()));
            } catch (SQLException e) {
                throw new ServletException("Could not load product", e);
            }
        }
        forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int companyId = (Integer) req.getSession().getAttribute("companyId");

        String idParam = req.getParameter("id");
        boolean editing = idParam != null && !idParam.trim().isEmpty();
        int id = editing ? parseId(idParam) : 0;
        if (editing && id < 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String name = clean(req.getParameter("productName"));
        String description = clean(req.getParameter("description"));
        String priceText = clean(req.getParameter("price"));
        String stockText = clean(req.getParameter("stock"));

        BigDecimal price = Validator.parseDecimal(priceText, MIN_PRICE, MAX_PRICE, 2);
        Integer stock = Validator.parseInt(stockText, 0, 100000);

        List<String> errors = new ArrayList<>();
        if (Validator.isBlank(name)) {
            errors.add("Product name is required.");
        } else if (Validator.tooLong(name, 150)) {
            errors.add("Product name must be at most 150 characters.");
        }
        if (Validator.tooLong(description, 1000)) {
            errors.add("Description must be at most 1000 characters.");
        }
        if (price == null) {
            errors.add("Price must be between 0.01 and 1000000, with at most 2 decimals.");
        }
        if (stock == null) {
            errors.add("Stock must be a whole number from 0 to 100000.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            if (editing) {
                req.setAttribute("editId", id);
            }
            fill(req, name, description, priceText, stockText);
            forward(req, resp);
            return;
        }

        try {
            // rule 5: only an approved company may list products
            // (it could have been blocked after it logged in)
            Company company = companyDAO.findById(companyId);
            if (company == null || !"APPROVED".equals(company.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/company/products?msg=notapproved");
                return;
            }

            Product p = new Product();
            p.setCompanyId(companyId);
            p.setProductName(name);
            p.setDescription(description);
            p.setPrice(price);
            p.setStock(stock);

            String msg;
            if (editing) {
                p.setProductId(id);
                msg = productDAO.update(p) ? "updated" : "invalid";
            } else {
                productDAO.insert(p);
                msg = "added";
            }
            resp.sendRedirect(req.getContextPath() + "/company/products?msg=" + msg);
        } catch (SQLException e) {
            getServletContext().log("Saving product failed", e);
            req.setAttribute("errors", List.of("Something went wrong. Please try again."));
            if (editing) {
                req.setAttribute("editId", id);
            }
            fill(req, name, description, priceText, stockText);
            forward(req, resp);
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/company/product-form.jsp").forward(req, resp);
    }

    private static void fill(HttpServletRequest req, String name, String description,
                             String price, String stock) {
        req.setAttribute("fName", name);
        req.setAttribute("fDescription", description);
        req.setAttribute("fPrice", price);
        req.setAttribute("fStock", stock);
    }

    private static int parseId(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String clean(String s) {
        return s == null ? null : s.trim();
    }
}