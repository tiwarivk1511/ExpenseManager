package com.android.roomdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Expense")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "Product")
    private String productName;

    @ColumnInfo(name = "Amount")
    private String amount;

    @ColumnInfo(name = "Date")
    private String date;


    // Default no-argument constructor required by Room
    public Expense() {

    }

    public Expense(int id, String productName, String amount, String date) {
        this.uid = id;
        this.productName = productName;
        this.amount = amount;
        this.date = date;
    }

    public Expense(String productName, String amount, String date) {

        this.productName = productName;
        this.amount = amount;
        this.date = date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {this.date = date;}

}
