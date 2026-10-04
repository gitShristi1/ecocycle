package com.ecocycle.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Guards every protected area:
 *   /user/*    needs role USER
 *   /company/* needs role COMPANY
 *   /admin/*   needs role ADMIN
 */
@WebFilter(urlPatterns = {"/user/*", "/company/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");

        // not logged in -> go to the login page for that area
        if (role == null) {
            resp.sendRedirect(req.getContextPath() + loginPageFor(req.getServletPath()));
            return;
        }

        // logged in, but the wrong kind of account -> 403 Forbidden
        String requiredRole = requiredRoleFor(req.getServletPath());
        if (requiredRole == null || !requiredRole.equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // stop the browser from caching protected pages
        // (so "Back" after logout does not show the old page)
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }
    
        private static String loginPageFor(String path) {
        if (isUnder(path, "/admin")) {
            return "/admin-login";
        }
        if (isUnder(path, "/company")) {
            return "/company-login";
        }
        return "/login";
    }
        
    private static String requiredRoleFor(String path) {
        if (isUnder(path, "/admin")) {
            return "ADMIN";
        }
        if (isUnder(path, "/company")) {
            return "COMPANY";
        }
        if (isUnder(path, "/user")) {
            return "USER";
        }
        return null;   // unknown protected path: deny by default
    }

    private static boolean isUnder(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}