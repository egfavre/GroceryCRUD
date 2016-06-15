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

    public static void deleteItemsTable(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items");
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM items");
        }
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

    public static Item selectItemByName(Connection conn, String itemName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE item_name = ?");
        stmt.setString(1, itemName);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String department = results.getString("department");
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
            return new Purchase(id, userId, itemId, qty, null);
        }
        return null;
    }

    public static ArrayList<Item> departmentList(Connection conn, String dept) throws SQLException {
        ArrayList<Item> deptList = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE ? = department");
        stmt.setString(1, dept);
        ResultSet results = stmt.executeQuery();
        while (results.next()){
            int id = results.getInt("id");
            String itemName = results.getString("item_name");
            String unityQty = results.getString("unit_qty");
            Double unitPrice = results.getDouble("unit_price");
            Item i = new Item(id, dept, itemName, unityQty, unitPrice);
            deptList.add(i);
        }
        return deptList;
    }

    public static ArrayList<Purchase> selectPurchasesByUser(Connection conn, String userName) throws SQLException {
        User user = selectUser(conn, userName);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchases INNER JOIN items ON purchases.item_id = items.id WHERE purchases.user_id = ?");
        stmt.setInt(1, user.id);
        ResultSet results = stmt.executeQuery();
        ArrayList<Purchase> purchaseList = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("purchases.id");
            int itemId = results.getInt("purchases.item_id");
            int qty = results.getInt("purchases.qty");
            String itemName = results.getString("items.item_name");
            Purchase p = new Purchase(id, user.id, itemId, qty, itemName);
            purchaseList.add(p);
        }
        return purchaseList;
    }

//    public static String selectItemNameFromPurchase(Connection conn, Purchase p) throws SQLException {
//        int itemId = p.itemId;
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items INNER JOIN purchases ON purchases.item_id = items.id WHERE purchases.item_id = ?");
//        stmt.setInt(1, itemId);
//        ResultSet results = stmt.executeQuery();
//        while (results.next()) {
//            String dept = results.getString("department");
//            String itemName = results.getString("item_name");
//            String unityQty = results.getString("unit_qty");
//            Double unitPrice = results.getDouble("unit_price");
//            Item currentItem = new Item(itemId, dept, itemName, unityQty, unitPrice);
//            return currentItem.itemName;
//        }
//        return null;
//    }

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

//        File f = new File("groceryFinal.csv");
//        Scanner fileScanner = new Scanner(f);
//        deleteItemsTable(conn);
//
//        fileScanner.nextLine();
//        while (fileScanner.hasNextLine()) {
//            String line = fileScanner.nextLine();
//            String[] columns = line.split(",");
//            String department = columns[1];
//            String itemName = columns[2];
//            String unitQty = columns[3];
//            double unitPrice = Double.valueOf(columns[4]);
//
//
//            insertItem(conn, department, itemName, unitQty, unitPrice);
//        }

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
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    HashMap b = new HashMap();
                    User user = selectUser(conn, userName);
                    boolean loggedIn = false;
                    if (user.userName.equals(userName)) {
                        loggedIn = true;
                    }
                    ArrayList<Item> deliList = departmentList(conn, "Deli");
                    ArrayList<Item> dairyList = departmentList(conn, "Dairy");
                    ArrayList<Item> produceList = departmentList(conn, "Produce");
                    ArrayList<Item> bakeryList = departmentList(conn, "Bakery");
                    ArrayList<Item> frozenList = departmentList(conn, "Frozen");

                    b.put("bakeryList", bakeryList);
                    b.put("dairyList", dairyList);
                    b.put("deliList", deliList);
                    b.put("frozenList", frozenList);
                    b.put("produceList", produceList);
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
//        Spark.post(
//                "/createShoppingList",
//                (request, response) -> {
//                    Session session = request.session();
//                    String userName = session.attribute("userName");
//                    User user = selectUser(conn, userName);
//
//                    ArrayList<Purchase> currentList = selectPurchasesByUser(conn, userName);
//                    ArrayList<String> shoppingListItem = new ArrayList<String>();
//                    ArrayList<ArrayList> shoppingList = new ArrayList<ArrayList>();
//
//                    for (Purchase p : currentList) {
//                        String itemName = selectItemNameFromPurchase(conn, p);
//                        shoppingListItem.add(itemName);
//                        shoppingListItem.add(String.valueOf(p.qty));
//                        shoppingList.add(shoppingListItem);}
//                    user.shoppingList = shoppingList;
//
//                        response.redirect("/shoppingList");
//                        return "";
//                    }
//
//        );

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
                    User user = selectUser(conn, userName);

                    ArrayList<Purchase> currentList = selectPurchasesByUser(conn, userName);

                    HashMap d = new HashMap();

                    d.put("shoppingList", currentList);
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

                    int purchaseId = Integer.valueOf(request.queryParams("id"));

                    deletePurchase(conn, purchaseId);
                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );
    }

}

