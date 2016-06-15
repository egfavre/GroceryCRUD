package com.egfavre;

/**
 * Created by user on 6/14/16.
 */
public class Purchase {
    int id;
    int userId;
    int itemId;
    int qty;
    String itemName;

    public Purchase(int id, int userId, int itemId, int qty, String itemName) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.qty = qty;
        this.itemName = itemName;
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

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", qty=" + qty +
                '}';
    }
}
