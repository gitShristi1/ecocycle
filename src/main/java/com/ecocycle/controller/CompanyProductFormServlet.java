package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.dao.ProductDAO;
import com.ecocycle.model.Company;
import com.ecocycle.model.Product;
import com.ecocycle.util.ImageStorage;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Access is checked by AuthFilter (role COMPANY).
@WebServlet("/company/product-form")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,        // bigger uploads are buffered on disk
        maxFileSize = 2 * 1024 * 1024,          // 2 MB per file
        maxRequestSize = 3 * 1024 * 1024)       // 3 MB for the whole form
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
                req.setAttribute("currentImage", p.getImagePath());
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

        // read the upload first: if it is over the size limit, the other form fields
        // cannot be read either, so we send the company back to the list
        Part imagePart;
        try {
            imagePart = req.getPart("image");
        } catch (IllegalStateException e) {
            resp.sendRedirect(req.getContextPath() + "/company/products?msg=toolarge");
            return;
        }

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

        // check the image: the real file type, not the name the browser claims
        byte[] imageData = null;
        String imageExt = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            imageData = imagePart.getInputStream().readAllBytes();
            imageExt = ImageStorage.detectType(imageData);
            if (imageExt == null) {
                errors.add("The image must be a JPEG or PNG file.");
            }
        }

        Product existing = null;
        try {
            if (editing) {
                // also proves this product belongs to this company
                existing = productDAO.findOwnedActive(id, companyId);
                if (existing == null) {
                    resp.sendRedirect(req.getContextPath() + "/company/products?msg=invalid");
                    return;
                }
            }

            if (!errors.isEmpty()) {
                showForm(req, resp, errors, editing ? id : null, existing,
                        name, description, priceText, stockText);
                return;
            }

            // rule 5: only an approved company may list products
            // (it could have been blocked after it logged in)
            Company company = companyDAO.findById(companyId);
            if (company == null || !"APPROVED".equals(company.getStatus())) {
                resp.sendRedirect(req.getContextPath() + "/company/products?msg=notapproved");
                return;
            }

            // everything is valid: save the file, then the database row
            String newImage = (imageData == null) ? null : ImageStorage.save(imageData, imageExt);

            Product p = new Product();
            p.setCompanyId(companyId);
            p.setProductName(name);
            p.setDescription(description);
            p.setPrice(price);
            p.setStock(stock);
            p.setImagePath(newImage);          // null keeps the current image when editing

            boolean saved;
            try {
                if (editing) {
                    p.setProductId(id);
                    saved = productDAO.update(p);
                } else {
                    productDAO.insert(p);
                    saved = true;
                }
            } catch (SQLException e) {
                if (newImage != null) {
                    ImageStorage.delete(newImage);   // do not leave an orphan file behind
                }
                throw e;
            }

            if (!saved && newImage != null) {
                ImageStorage.delete(newImage);
            }
            if (saved && editing && newImage != null && existing.getImagePath() != null) {
                ImageStorage.delete(existing.getImagePath());   // the replaced picture
            }

            String msg = !saved ? "invalid" : (editing ? "updated" : "added");
            resp.sendRedirect(req.getContextPath() + "/company/products?msg=" + msg);
        } catch (SQLException e) {
            getServletContext().log("Saving product failed", e);
            showForm(req, resp, List.of("Something went wrong. Please try again."),
                    editing ? id : null, existing, name, description, priceText, stockText);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, List<String> errors,
                          Integer editId, Product existing, String name, String description,
                          String price, String stock) throws ServletException, IOException {
        req.setAttribute("errors", errors);
        if (editId != null) {
            req.setAttribute("editId", editId);
        }
        if (existing != null) {
            req.setAttribute("currentImage", existing.getImagePath());
        }
        fill(req, name, description, price, stock);
        forward(req, resp);
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