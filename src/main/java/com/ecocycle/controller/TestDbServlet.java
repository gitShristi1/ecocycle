package com.ecocycle.controller;

import com.ecocycle.util.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/test-db")
public class TestDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT 'Connected to Oracle' FROM dual")) {
            if (rs.next()) {
                out.println(rs.getString(1));
            }
        } catch (Exception e) {
            out.println("Connection failed: " + e);
        }
    }
}