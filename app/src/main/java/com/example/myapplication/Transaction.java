package com.example.myapplication;

public class Transaction {
    private int id;
    private String type;
    private String description;
    private String amount;
    private String date;

    // 생성자
    public Transaction(int id, String type, String description, String amount, String date) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.date = date; // 날짜 초기화
    }

    // 기본 생성자
    public Transaction(){
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    // Getter와 Setter 메서드
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() { // 날짜에 대한 getter
        return date;
    }

    public void setDate(String date) { // 날짜에 대한 setter
        this.date = date;
    }
}
