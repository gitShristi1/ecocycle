package com.ecocycle.controller;

import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.User;
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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_TAKEN = "An account with this email already exists.";

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String fullName = clean(req.getParameter("fullName"));
        String email = clean(req.getParameter("email"));
        String password = req.getParameter("password");          // never trimmed
        String confirm = req.getParameter("confirmPassword");
        String phone = clean(req.getParameter("phone"));
        String address = clean(req.getParameter("address"));
        String city = clean(req.getParameter("city"));

        List<String> errors = new ArrayList<>();

        if (Validator.isBlank(fullName)) {
            errors.add("Full name is required.");
        } else if (Validator.tooLong(fullName, 100)) {
            errors.add("Full name must be at most 100 characters.");
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
            if (userDAO.emailExists(email)) {
                showErrors(req, resp, List.of(EMAIL_TAKEN));
                return;
            }

            User u = new User();
            u.setFullName(fullName);
            u.setEmail(email);
            u.setPasswordHash(PasswordUtil.hash(password));
            u.setPhone(phone);
            u.setAddress(address);
            u.setCity(city);
            userDAO.insert(u);

            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                // ORA-00001 (unique constraint): someone registered this email a moment ago
                showErrors(req, resp, List.of(EMAIL_TAKEN));
            } else {
                getServletContext().log("Registration failed", e);
                showErrors(req, resp, List.of("Something went wrong. Please try again."));
            }
        }
    }

    private void showErrors(HttpServletRequest req, HttpServletResponse resp, List<String> errors)
            throws ServletException, IOException {
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private static String clean(String s) {
        return s == null ? null : s.trim();
    }
}