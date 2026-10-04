package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.model.Company;
import com.ecocycle.util.PasswordUtil;
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

@WebServlet("/register-company")
public class CompanyRegisterServlet extends HttpServlet {

    private static final String EMAIL_TAKEN = "A company with this email already exists.";

    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register-company.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String companyName = clean(req.getParameter("companyName"));
        String email = clean(req.getParameter("email"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");
        String phone = clean(req.getParameter("phone"));
        String address = clean(req.getParameter("address"));
        String city = clean(req.getParameter("city"));

        List<String> errors = new ArrayList<>();

        if (Validator.isBlank(companyName)) {
            errors.add("Company name is required.");
        } else if (Validator.tooLong(companyName, 150)) {
            errors.add("Company name must be at most 150 characters.");
        }

        if (Validator.isBlank(email)) {
            errors.add("Email is required.");
        } else if (Validator.tooLong(email, 150) || !Validator.isValidEmail(email)) {
            errors.add("Enter a valid email address.");
        }

        if (Validator.isBlank(password)) {
            errors.add("Password is required.");
        } else if (!Validator.isStrongPassword(password)) {
            errors.add("Password must be 8 to 72 characters and include at least one letter and one digit.");
        } else if (!password.equals(confirm)) {
            errors.add("Passwords do not match.");
        }

        if (!Validator.isBlank(phone) && !Validator.isValidPhone(phone)) {
            errors.add("Phone number must be exactly 10 digits.");
        }
        if (Validator.tooLong(address, 300)) {
            errors.add("Address must be at most 300 characters.");
        }
        if (Validator.tooLong(city, 80)) {
            errors.add("City must be at most 80 characters.");
        }

        if (!errors.isEmpty()) {
            showErrors(req, resp, errors);
            return;
        }

        try {
            if (companyDAO.emailExists(email)) {
                showErrors(req, resp, List.of(EMAIL_TAKEN));
                return;
            }

            Company c = new Company();
            c.setCompanyName(companyName);
            c.setEmail(email);
            c.setPasswordHash(PasswordUtil.hash(password));
            c.setPhone(phone);
            c.setAddress(address);
            c.setCity(city);
            companyDAO.insert(c);      // status starts as PENDING

            resp.sendRedirect(req.getContextPath() + "/company-login?registered=1");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                showErrors(req, resp, List.of(EMAIL_TAKEN));
            } else {
                getServletContext().log("Company registration failed", e);
                showErrors(req, resp, List.of("Something went wrong. Please try again."));
            }
        }
    }

    private void showErrors(HttpServletRequest req, HttpServletResponse resp, List<String> errors)
            throws ServletException, IOException {
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/WEB-INF/views/register-company.jsp").forward(req, resp);
    }

    private static String clean(String s) {
        return s == null ? null : s.trim();
    }
}