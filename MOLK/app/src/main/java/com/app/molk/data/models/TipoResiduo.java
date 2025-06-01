package com.app.molk.data.models;

import com.google.gson.annotations.SerializedName;

public class TipoResiduo {
    @SerializedName("tipo")
    private String tipo;

    @SerializedName("quantidade")
    private int quantidade;

    // Construtor vazio necessário para o Gson
    public TipoResiduo() {
    }

    // Construtor com parâmetros (útil para testes)
    public TipoResiduo(String tipo, int quantidade) {
        this.tipo = tipo;
        this.quantidade = quantidade;
    }

    // getters e setters
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "TipoResiduo{" +
                "tipo='" + tipo + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}