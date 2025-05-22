package com.app.molk.data.models;

import java.util.List;

public class ResiduoParceiro {
    public int id;
    public int id_usuario;
    public String tipo;
    public String descricao;
    public Usuario usuario;
    public ImagemResiduo imagem_residuo;

    public static class Usuario {
        public String nome_empresa;
    }

    public static class ImagemResiduo {
        public String type;
        public List<Integer> data;
    }
}
