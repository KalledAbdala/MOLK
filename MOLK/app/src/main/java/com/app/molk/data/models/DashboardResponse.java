package com.app.molk.data.models;

// DashboardResponse.java
public class DashboardResponse<T> {
    private int status;
    private T data;

    public int getStatus() { return status; }
    public T getData() { return data; }
}

