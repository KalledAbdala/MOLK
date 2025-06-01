package com.app.molk.data.models;

public class EmpresaResponse {
    private String nome_empresa;
    private int quantidade_residuos;
    private int quantidade_interesse;

    public String getNomeEmpresa() { return nome_empresa; }
    public int getQuantidadeResiduos() { return quantidade_residuos; }
    public int getQuantidadeInteresse() { return quantidade_interesse; }
}
