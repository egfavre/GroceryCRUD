package com.egfavre;

/**
 * Created by user on 6/9/16.
 */
public class Item {
    String id;
    String department;
    String itemName;
    String unitQuantity;
    String unitPrice;

    public Item(String id, String department, String itemName, String unitQuantity, String unitPrice) {
        this.id = id;
        this.department = department;
        this.itemName = itemName;
        this.unitQuantity = unitQuantity;
        this.unitPrice = unitPrice;
    }
}
