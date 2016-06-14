package com.egfavre;

/**
 * Created by user on 6/14/16.
 */
public class Purchase {
    int id;
    int userId;
    int itemId;
    int qty;

    public Purchase(int id, int userId, int itemId, int qty) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.qty = qty;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQty() {
        return qty;
    }
}
