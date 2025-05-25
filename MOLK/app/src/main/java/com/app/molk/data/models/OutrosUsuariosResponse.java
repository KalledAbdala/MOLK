package com.app.molk.data.models;

import java.util.List;

public class OutrosUsuariosResponse {
    private String message;
    private List<User> usuarios;

    public String getMessage() {
        return message;
    }

    public List<User> getUsuarios() {
        return usuarios;
    }
}
