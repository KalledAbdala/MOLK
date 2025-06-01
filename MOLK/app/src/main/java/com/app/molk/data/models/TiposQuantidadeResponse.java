package com.app.molk.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TiposQuantidadeResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("tipos")
    private List<TipoResiduo> tipos;

    public String getMessage() {
        return message;
    }

    public List<TipoResiduo> getTipos() {
        return tipos;
    }
}