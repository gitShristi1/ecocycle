package com.ecocycle.controller;

import com.ecocycle.dao.UserDAO;
import com.ecocycle.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

// Access is checked by AuthFilter (role ADMIN).
@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("users", userDAO.search(req.getParameter("q")));
        } catch (SQLException e) {
            throw new ServletException("Could not load users", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer userId = Validator.parseInt(req.getParameter("userId"), 1, Integer.MAX_VALUE);
        String action = req.getParameter("action");

        String expected;
        String next;
        if ("block".equals(action)) {
            expected = "ACTIVE";
            next = "BLOCKED";
        } else if ("unblock".equals(action)) {
            expected = "BLOCKED";
            next = "ACTIVE";
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (userId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String q = req.getParameter("q");
        q = (q == null || q.length() > 100) ? "" : q.trim();

        try {
            boolean changed = userDAO.updateStatusIfCurrent(userId, expected, next);
            // redirect after POST, keeping the search the admin was looking at
            resp.sendRedirect(req.getContextPath() + "/admin/users?msg=" + (changed ? action : "stale")
                    + "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8));
        } catch (SQLException e) {
            throw new ServletException("Could not update the user", e);
        }
    }
}