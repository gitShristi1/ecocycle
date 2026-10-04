package com.ecocycle.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() { }

    /** Starts a fresh session for a logged-in account (new id prevents session fixation). */
    public static HttpSession startSession(HttpServletRequest req, String role) {
        req.getSession();            // make sure a session exists
        req.changeSessionId();       // then give it a new id
        HttpSession session = req.getSession();
        session.setAttribute("role", role);
        return session;
    }
}