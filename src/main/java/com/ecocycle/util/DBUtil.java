package com.ecocycle.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }

        try (InputStream in = DBUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found");
            }
            Properties props = new Properties();
            props.load(in);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}