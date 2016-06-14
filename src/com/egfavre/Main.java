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

    public static void  insertPurchase(Connection conn, int userId, int itemId, int qty) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO purchases VALUES (NULL, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setInt(2, itemId);
        stmt.setInt(3, qty);
        stmt.execute();
    }

    public static User selectUser (Connection conn, String userName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?");
        stmt.setString(1,userName);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, userName, password);
        }
        return null;
    }

    public static Item selectItem (Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM items WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            String department = results.getString("department");
            String itemName = results.getString("item_name");
            String unitQty = results.getString("unit_qty");
            double unitPrice = results.getDouble("unit_price");
            return new Item(id, department, itemName, unitQty, unitPrice);
        }
        return null;
    }

    public static Purchase selectPurchase (Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchases WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            int userId = results.getInt("user_id");
            int itemId = results.getInt("item_id");
            int qty = results.getInt("qty");
            return new Purchase(id, userId, itemId, qty);
        }
        return null;
    }

    public static ArrayList<Purchase> selectPurchasesByUser(Connection conn, String userName) throws SQLException {
        User user = selectUser(conn, userName);
        int thisId = user.id;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM purchases INNER JOIN users ON purchases.user_id = users.id WHERE users.id = ?");
        stmt.setInt(1, thisId);
        ResultSet results = stmt.executeQuery();
        ArrayList<Purchase> purchaseList= new ArrayList<>();
        while(results.next()){
            int id = results.getInt("purchases.id");
            int userId = results.getInt("purchases.user_id");
            int itemId = results.getInt("purchases.item_id");
            int qty = results.getInt("purchases.qty");
            Purchase p = new Purchase(id, userId, itemId, qty);
            purchaseList.add(p);
        }
        return purchaseList;
    }

    public static void updatePurchase (Connection conn, int newQty, int purchaseId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE purchases SET qty = ? WHERE id = ?");
        stmt.setInt(1, newQty);
        stmt.setInt(2, purchaseId);
        stmt.execute();
    }


//    public static ArrayList<Message> selectReplies(Connection conn, int replyId) throws SQLException {
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages INNER JOIN users ON messages.user_id = users.id WHERE messages.reply_id = ?");
//        stmt.setInt(1, replyId);
//        ResultSet results = stmt.executeQuery();
//        ArrayList<Message> msgs = new ArrayList<>();
//        while(results.next()){
//            int id = results.getInt("id");
//            String text = results.getString("messages.text");
//            String author = results.getString("users.name");
//            Message msg = new Message(id, replyId, author, text);
//            msgs.add(msg);
//        }
//        return msgs;
//    }

    public static void main(String[] args) throws FileNotFoundException, SQLException {
  /*      File f = new File("groceryFinal.csv");
        Scanner fileScanner = new Scanner(f);
        ArrayList<Item> items = new ArrayList<>();
        HashMap<String, User> users = new HashMap<>();

        fileScanner.nextLine();
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split(",");
            Item item = new Item(columns[0], columns[1], columns[2], columns[3], columns[4], "0");
            items.add(item);
        }

        ArrayList<Item> deliList = new ArrayList<>();
        ArrayList<Item> dairyList = new ArrayList<>();
        ArrayList<Item> produceList = new ArrayList<>();
        ArrayList<Item> bakeryList = new ArrayList<>();
        ArrayList<Item> frozenList = new ArrayList<>();


        for (Item item:items) {

            if (item.department.equals("Deli")) {
                deliList.add(item);
            }
            if (item.department.equals("Dairy")) {
                dairyList.add(item);
            }
            if (item.department.equals("Produce")) {
                produceList.add(item);
            }
            if (item.department.equals("Bakery")) {
                bakeryList.add(item);
            }
            if (item.department.equals("Frozen")) {
                frozenList.add(item);
            }
        }*/

//create server connection and tables
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

/*
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
                    if (userName == null || password == null){
                        throw new Exception("Not Valid login");
                    }

                    User user = users.get(userName);
                    if (user == null) {
                        user = new User(userName, password);
                        users.put(userName, user);
                    }
                    Session session = request.session();
                    session.attribute("userName", userName);
                    System.out.println(users);
                    response.redirect("/viewItems");
                    return "";
                }
        );


//display viewItems page
        Spark.get(
                "/viewItems",
                (request, response) -> {
                    HashMap b = new HashMap();
                    boolean loggedIn = false;
                    if (!users.isEmpty()) {
                        loggedIn = true;
                    }

                    b.put("items", items);
                    b.put("deliList", deliList);
                    b.put("dairyList", dairyList);
                    b.put("produceList", produceList);
                    b.put("bakeryList", bakeryList);
                    b.put("frozenList", frozenList);
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
                    User user = users.get(userName);

                    String qty = request.queryParams("qty");
                    String id = request.queryParams("id");

                    if (qty.isEmpty()){
                        qty = "0";
                    }
                    qty = request.queryParams("qty");
                    ArrayList<String> idQty = new ArrayList<String>();
                    idQty.add(id);
                    idQty.add(qty);
                    user.shoppingList.add(idQty);

                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );
        Spark.post(
                "/createShoppingList",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = users.get(userName);

                    for (ArrayList idQty: user.shoppingList) {
                        String idStr = (String.valueOf(idQty.get(0)));
                        int id = (Integer.valueOf(idStr));
                        items.get(id-1).setQty((String) idQty.get(1));
                        user.currentList.add(items.get(id-1));
                    }
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
                    User user = users.get(userName);
                    HashMap d = new HashMap();
                    d.put("items", items);
                    d.put("currentList", user.currentList);
                    return new ModelAndView(d, "shoppingList.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/updateQty",
                (request, response) -> {
                    String updateQty = request.queryParams("updateQty");
                    String id = request.queryParams("id");
                    for (Item item:items){
                        if (item.id.equals(id)){
                            item.qty = updateQty;
                        }
                    }
                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );

        Spark.post(
                "/delete",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = users.get(userName);

                    String id = request.queryParams("id");
                    for (Item item:items){
                        if (item.id.equals(id)){
                            user.currentList.remove(item);
                        }
                    }
                    response.redirect(request.headers("Referer"));
                    return "";
                }
        );*/
    }
}
