package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.Company;
import com.ecocycle.model.User;
import com.ecocycle.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String INVALID = "Invalid email or password.";

    private final UserDAO userDAO = new UserDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String role = req.getParameter("role");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            if ("COMPANY".equals(role)) {
                loginCompany(req, resp, email, password);
            } else {
                loginUser(req, resp, email, password);
            }
        } catch (SQLException e) {
            getServletContext().log("Login failed", e);
            showError(req, resp, "Something went wrong. Please try again.");
        }
    }

    private void loginUser(HttpServletRequest req, HttpServletResponse resp,
                           String email, String password)
            throws SQLException, ServletException, IOException {
        User user = userDAO.findByEmail(email);

        // same message for "no such email" and "wrong password"
        if (user == null || !PasswordUtil.matches(password, user.getPasswordHash())) {
            showError(req, resp, INVALID);
            return;
        }
        if ("BLOCKED".equals(user.getStatus())) {
            showError(req, resp, "Your account has been blocked. Please contact the admin.");
            return;
        }

        HttpSession session = startSession(req, "USER");
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getFullName());
        resp.sendRedirect(req.getContextPath() + "/user/dashboard");
    }

    private void loginCompany(HttpServletRequest req, HttpServletResponse resp,
                              String email, String password)
            throws SQLException, ServletException, IOException {
        Company company = companyDAO.findByEmail(email);

        if (company == null || !PasswordUtil.matches(password, company.getPasswordHash())) {
            showError(req, resp, INVALID);
            return;
        }

        // the status is only revealed after the password was correct
        String status = company.getStatus();
        if ("PENDING".equals(status)) {
            showError(req, resp, "Your registration is waiting for admin approval. Please try again later.");
            return;
        }
        if ("REJECTED".equals(status)) {
            showError(req, resp, "Your registration was not approved. Please contact the admin.");
            return;
        }
        if ("BLOCKED".equals(status)) {
            showError(req, resp, "Your account has been blocked. Please contact the admin.");
            return;
        }

        HttpSession session = startSession(req, "COMPANY");
        session.setAttribute("companyId", company.getCompanyId());
        session.setAttribute("companyName", company.getCompanyName());
        resp.sendRedirect(req.getContextPath() + "/company/dashboard");
    }

    /** Starts a fresh session (new id after login prevents session fixation). */
    private HttpSession startSession(HttpServletRequest req, String role) {
        req.getSession();            // make sure a session exists
        req.changeSessionId();       // then give it a new id
        HttpSession session = req.getSession();
        session.setAttribute("role", role);
        return session;
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}