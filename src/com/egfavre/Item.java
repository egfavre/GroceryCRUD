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
    String qty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUnitQuantity() {
        return unitQuantity;
    }

    public void setUnitQuantity(String unitQuantity) {
        this.unitQuantity = unitQuantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", department='" + department + '\'' +
                ", itemName='" + itemName + '\'' +
                ", unitQuantity='" + unitQuantity + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                '}';
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Item(String id, String department, String itemName, String unitQuantity, String unitPrice, String qty) {
        this.id = id;
        this.department = department;
        this.itemName = itemName;
        this.unitQuantity = unitQuantity;
        this.unitPrice = unitPrice;
        this.qty = qty;
    }
}

