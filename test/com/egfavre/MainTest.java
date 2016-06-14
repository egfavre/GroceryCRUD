package com.egfavre;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by user on 6/14/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alice", "");
        User user = Main.selectUser(conn, "Alice");
        conn.close();

        assertTrue(user != null);
    }

    @Test
    public void testItem() throws SQLException {
        Connection conn = startConnection();
        Main.insertItem(conn, "dept", "name", "each", 8.00);
        Item item = Main.selectItem(conn, 1);
        conn.close();

        assertTrue(item != null);
    }

    @Test
    public void testPurchase() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "alice", "");
        Main.insertItem(conn, "", "", "", 10.0);
        Main.insertPurchase(conn, 1, 1, 10);
        Purchase purchase = Main.selectPurchase(conn, 1);
        conn.close();

        assertTrue(purchase != null);
    }

}