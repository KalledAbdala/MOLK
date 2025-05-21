package com.app.molk.data.models;

public class NovoStatusBody {
    private String novoStatus;

    public NovoStatusBody(String status) {
        this.novoStatus = status;
    }
    public String getStatus() {
        return novoStatus;
    }

    public void setStatus(String status) {
        this.novoStatus = status;
    }

}
