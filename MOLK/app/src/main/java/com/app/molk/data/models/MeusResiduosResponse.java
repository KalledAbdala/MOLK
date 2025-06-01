// MeusResiduosResponse.java
package com.app.molk.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MeusResiduosResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("residuos")
    private List<Residuo> residuos;

    public String getMessage() {
        return message;
    }

    public List<Residuo> getResiduos() {
        return residuos;
    }

    public static class Residuo {
        @SerializedName("id")
        private int id;

        @SerializedName("tipo")
        private String tipo;

        @SerializedName("quantidade")
        private int quantidade;

        @SerializedName("unidade")
        private String unidade;

        @SerializedName("status_residuo")
        private String status_residuo;

        @SerializedName("nome_empresa_interessada")
        private String nome_empresa_interessada;

        // Getters
        public int getId() {
            return id;
        }

        public String getTipo() {
            return tipo;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public String getUnidade() {
            return unidade;
        }

        public String getStatus_residuo() {
            return status_residuo;
        }

        public String getNome_empresa_interessada() {
            return nome_empresa_interessada;
        }
    }
}