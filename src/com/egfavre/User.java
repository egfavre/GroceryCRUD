package com.egfavre;

import java.util.ArrayList;

/**
 * Created by user on 6/10/16.
 */
public class User {
    String username;
    String password;
    ArrayList shoppingList;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ArrayList shoppingList) {
        this.shoppingList = shoppingList;
    }
}
