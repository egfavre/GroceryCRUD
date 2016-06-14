package com.egfavre;

import java.util.ArrayList;

/**
 * Created by user on 6/10/16.
 */
public class User {
    int id;
    String userName;
    String password;
    ArrayList<ArrayList> shoppingList = new ArrayList<>();


    public User(int id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<ArrayList> getShoppingList() {
        return shoppingList;
    }
}
