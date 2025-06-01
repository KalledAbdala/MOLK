// EmpresaInteresse.java
package com.app.molk.data.models;

import com.google.gson.annotations.SerializedName;

public class EmpresaInteresse {
    @SerializedName("nome_empresa")
    private String nomeEmpresa;

    @SerializedName("quantidade_interesse")
    private int quantidadeInteresse;

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public int getQuantidadeInteresse() {
        return quantidadeInteresse;
    }
}