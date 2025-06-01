package com.app.molk.data.models;

import java.util.List;

public class EmpresaInteresseResponse {
    private String message;
    private List<EmpresaInteresse> empresas;

    public String getMessage() {
        return message;
    }

    public List<EmpresaInteresse> getEmpresas() {
        return empresas;
    }

    public static class EmpresaInteresse {
        private String nome_empresa;
        private int quantidade_interesse;

        public String getNomeEmpresa() {
            return nome_empresa;
        }

        public int getQuantidadeInteresse() {
            return quantidade_interesse;
        }
    }
}