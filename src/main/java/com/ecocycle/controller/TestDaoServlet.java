package com.ecocycle.controller;

import com.ecocycle.dao.AdminDAO;
import com.ecocycle.dao.CompanyDAO;
import com.ecocycle.dao.UserDAO;
import com.ecocycle.model.Admin;
import com.ecocycle.model.Company;
import com.ecocycle.model.User;
import com.ecocycle.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Temporary test page. We will delete it once real login/register pages exist.
@WebServlet("/test-dao")
public class TestDaoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            testUser(out);
            out.println();
            testCompany(out);
            out.println();
            testAdmin(out);
        } catch (Exception e) {
            out.println("Error: " + e);
        }
    }

    private void testUser(PrintWriter out) throws Exception {
        out.println("== USER ==");
        UserDAO dao = new UserDAO();
        String email = "test@example.com";

        if (!dao.emailExists(email)) {
            User u = new User();
            u.setFullName("Test User");
            u.setEmail(email);
            u.setPasswordHash(PasswordUtil.hash("Test@123"));
            u.setPhone("9999999999");
            u.setAddress("1 Test Street");
            u.setCity("TestCity");
            out.println("Inserted user with id: " + dao.insert(u));
        } else {
            out.println("Test user already exists, skipping insert");
        }

        User found = dao.findByEmail(email);
        out.println("Found: " + found.getFullName() + " / status=" + found.getStatus());
        out.println("Correct password matches: "
                + PasswordUtil.matches("Test@123", found.getPasswordHash()));
        out.println("Wrong password matches:   "
                + PasswordUtil.matches("wrong", found.getPasswordHash()));
    }

    private void testCompany(PrintWriter out) throws Exception {
        out.println("== COMPANY ==");
        CompanyDAO dao = new CompanyDAO();
        String email = "testcompany@example.com";

        if (!dao.emailExists(email)) {
            Company c = new Company();
            c.setCompanyName("Test Recyclers Ltd");
            c.setEmail(email);
            c.setPasswordHash(PasswordUtil.hash("Test@123"));
            c.setPhone("8888888888");
            c.setAddress("2 Test Road");
            c.setCity("TestCity");
            out.println("Inserted company with id: " + dao.insert(c));
        } else {
            out.println("Test company already exists, skipping insert");
        }

        Company found = dao.findByEmail(email);
        out.println("Found: " + found.getCompanyName() + " / status=" + found.getStatus());

        dao.updateStatus(found.getCompanyId(), "APPROVED");
        out.println("After approval, status="
                + dao.findById(found.getCompanyId()).getStatus());
        out.println("Approved companies in database: "
                + dao.findByStatus("APPROVED").size());
    }

    private void testAdmin(PrintWriter out) throws Exception {
        out.println("== ADMIN ==");
        AdminDAO dao = new AdminDAO();
        String email = "testadmin@example.com";

        if (!dao.emailExists(email)) {
            Admin a = new Admin();
            a.setFullName("Test Admin");
            a.setEmail(email);
            a.setPasswordHash(PasswordUtil.hash("Test@123"));
            out.println("Inserted admin with id: " + dao.insert(a));
        } else {
            out.println("Test admin already exists, skipping insert");
        }

        Admin found = dao.findByEmail(email);
        out.println("Found: " + found.getFullName() + " / " + found.getEmail());
    }
}
