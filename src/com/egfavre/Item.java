package com.egfavre;

/**
 * Created by user on 6/9/16.
 */
public class Item {
    int id;
    String department;
    String itemName;
    String unitQty;
    double unitPrice;


    public Item(int id, String department, String itemName, String unitQty, double unitPrice) {
        this.id = id;
        this.department = department;
        this.itemName = itemName;
        this.unitQty = unitQty;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(String unitQty) {
        this.unitQty = unitQty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", department='" + department + '\'' +
                ", itemName='" + itemName + '\'' +
                ", unitQty='" + unitQty + '\'' +
                ", unitPrice='" + unitPrice + '\'' +
                '}';
    }
}

