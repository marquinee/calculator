package org.example.calculator.model;

import java.time.LocalDateTime;

public class HistoryEntry {
    private int id;
    private int userId;
    private String type;
    private String inputParams;
    private String result;
    private LocalDateTime createdAt;

    public HistoryEntry(int id, int userId, String type, String inputParams, String result, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.inputParams = inputParams;
        this.result = result;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public String getInputParams() { return inputParams; }
    public String getResult() { return result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

