package com.ecocycle.controller;

import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.model.Company;
import com.ecocycle.util.PasswordUtil;
import com.ecocycle.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/company-login")
public class CompanyLoginServlet extends HttpServlet {

    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/company-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Company company = companyDAO.findByEmail(email);

            if (company == null || !PasswordUtil.matches(password, company.getPasswordHash())) {
                showError(req, resp, "Invalid email or password.");
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

            HttpSession session = SessionUtil.startSession(req, "COMPANY");
            session.setAttribute("companyId", company.getCompanyId());
            session.setAttribute("companyName", company.getCompanyName());
            resp.sendRedirect(req.getContextPath() + "/company/dashboard");
        } catch (SQLException e) {
            getServletContext().log("Company login failed", e);
            showError(req, resp, "Something went wrong. Please try again.");
        }
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.getRequestDispatcher("/WEB-INF/views/company-login.jsp").forward(req, resp);
    }
}