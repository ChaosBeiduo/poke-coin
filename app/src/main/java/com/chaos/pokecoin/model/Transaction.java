package com.chaos.pokecoin.model;

import java.util.Date;

public class Transaction {
    private String type;  // "收入" 或 "支出"
    private double amount;
    private Date date;
    private long id;
    private Integer userId;

    // 用于创建新交易的简单构造函数
    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.date = new Date();
    }

    // 从数据库加载时使用的完整构造函数
    public Transaction(long id, String type, double amount, Date date, Integer userId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
    }

    // Getters
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public long getId() { return id; }
    public Integer getUserId() { return userId; }
} 