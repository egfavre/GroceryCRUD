package com.egfavre;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, user_name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS items (id IDENTITY, department VARCHAR, item_name VARCHAR, unit_qty VARCHAR, unit_price DOUBLE)");
        stmt.execute("CREATE TABLE IF NOT EXISTS purchases (id IDENTITY, user_id INT, item_id INT, qty INT)");
    }

    public static void insertUser(Connection conn, String userName, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, userName);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static void insertItem(Connection conn, String department, String itemName, String unitQty, double unitPrice) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO items VALUES (NULL, ?, ?, ?, ?)");
        stmt.setString(1, department);
        stmt.setString(2, itemName);
        stmt.setString(3, unitQty);
        stmt.setDouble(4, unitPrice);
        stmt.execute();
    }

    public static void insertPurchase(Connection conn, int userId, int itemId, int qty) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO purchases VALUES (NULL, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setInt(2, itemId);
        stmt.setInt(3, qty);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1, userName);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, userName, password);
        }
        return null;
    }

    public static Item selectItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String department = results.getString("department");
            String itemName = results.getString("item_name");
            String unitQty = results.getString("unit_qty");
            double unitPrice = results.getDouble("unit_price");
            return new Item(id, department, itemName, unitQty, unitPrice);
        }
        return null;
    }

    public static Purchase selectPurchase(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchases WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int userId = results.getInt("user_id");
            int itemId = results.getInt("item_id");
            int qty = results.getInt("qty");
            return new Purchase(id, userId, itemId, qty);
        }
        return null;
    }

    public static ArrayList<Item> departmentList(Connection conn, String department) throws SQLException {
        ArrayList<Item> deptList = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE department = ?");
        stmt.setString(1, department);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String itemName = results.getString("item_name");
            String unityQty = results.getString("unit_qty");
            Double unitPrice = results.getDouble("unit_price");
            Item i = new Item(id, department, itemName, unityQty, unitPrice);
            deptList.add(i);
        }
        return deptList;
    }

    public static ArrayList<Purchase> selectPurchasesByUser(Connection conn, String userName) throws SQLException {
        User user = selectUser(conn, userName);
        int thisId = user.id;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchases INNER JOIN users ON purchases.user_id = users.id WHERE users.id = ?");
        stmt.setInt(1, thisId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Purchase> purchaseList = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("purchases.id");
            int userId = results.getInt("purchases.user_id");
            int itemId = results.getInt("purchases.item_id");
            int qty = results.getInt("purchases.qty");
            Purchase p = new Purchase(id, userId, itemId, qty);
            purchaseList.add(p);
        }
        return purchaseList;
    }

    public static void updatePurchase(Connection conn, int newQty, int purchaseId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE purchases SET qty = ? WHERE id = ?");
        stmt.setInt(1, newQty);
        stmt.setInt(2, purchaseId);
        stmt.execute();
    }

    public static void deletePurchase(Connection conn, int purchaseId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM purchases WHERE id = ?");
        stmt.setInt(1, purchaseId);
        stmt.execute();
    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        File f = new File("groceryFinal.csv");
        Scanner fileScanner = new Scanner(f);

        fileScanner.nextLine();
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            String department = columns[1];
            String itemName = columns[2];
            String unitQty = columns[3];
            double unitPrice = Double.valueOf(columns[4]);

            insertItem(conn, department, itemName, unitQty, unitPrice);
        }

//display welcome page
        Spark.staticFileLocation("public");
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap a = new HashMap<>();
                    return new ModelAndView(a, "welcome.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String userName = request.queryParams("userName");
                    String password = request.queryParams("password");
                    if (userName == null || password == null) {
                        throw new Exception("Not Valid login");
                    }

                    User user = selectUser(conn, userName);
                    if (user == null) {
                        insertUser(conn, userName, password);
                    }


                    Session session = request.session();
                    session.attribute("userName", userName);

                    response.redirect("/viewItems");
                    return "";
                }
        );


//display viewItems page
        Spark.get(
                "/viewItems",
                (request, response) -> {
                    HashMap b = new HashMap();
                    String username = request.queryParams("userName");
                    User user = selectUser(conn, username);
                    boolean loggedIn = false;
                    if (user.userName.equals(username)) {
                        loggedIn = true;
                    }

                    b.put("deliList", departmentList(conn, "deli"));
                    b.put("dairyList", departmentList(conn, "dairy"));
                    b.put("produceList", departmentList(conn, "produce"));
                    b.put("bakeryList", departmentList(conn, "bakery"));
                    b.put("frozenList", departmentList(conn, "frozen"));
                    b.put("loggedIn", loggedIn);
                    return new ModelAndView(b, "viewItems.html");
                },
                new MustacheTemplateEngine()
        );


        Spark.post(
                "/quantity",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    User user = selectUser(conn, userName);
                    int userId = user.id;
                    int itemId = Integer.valueOf(request.queryParams("id"));
                    int qty = Integer.valueOf(request.queryParams("qty"));

                    insertPurchase(conn, userId, itemId, qty);

                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );
        Spark.post(
                "/createShoppingList",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    selectPurchasesByUser(conn, userName);
                    response.redirect("/shoppingList");
                    return "";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );

        Spark.get(
                "/shoppingList",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    ArrayList<Purchase> purchList = selectPurchasesByUser(conn, userName);
                    for (Purchase p : purchList) {
                        String qty = String.valueOf(p.qty);
                        Item i = selectItem(conn, p.getItemId());
                        String itemName = i.itemName;
                        double price = i.unitPrice;
                        String $ = "$ " + String.valueOf(price);
                        User user = selectUser(conn, userName);
                        ArrayList<String> listItem = new ArrayList();
                        listItem.add(0, qty);
                        listItem.add(1, itemName);
                        listItem.add(2, $);
                        user.shoppingList.add(listItem);
                    }
                    HashMap d = new HashMap();
                    d.put("shoppingList", selectUser(conn, userName).shoppingList);
                    return new ModelAndView(d, "shoppingList.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/updateQty",
                (request, response) -> {
                    int newQty = Integer.valueOf(request.queryParams("updateQty"));
                    int id = Integer.valueOf(request.queryParams("id"));
                    updatePurchase(conn, newQty, id);

                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );

        Spark.post(
                "/delete",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    int purchaseId = Integer.valueOf(request.queryParams("purchaseId"));

                    deletePurchase(conn, purchaseId);
                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );
    }

}

