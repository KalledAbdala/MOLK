package com.app.molk.data.models;

import java.util.List;

public class Residuo {
    private String tipo;
    private String descricao;
    private ImagemResiduo imagem_residuo;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public ImagemResiduo getImagem_residuo() { return imagem_residuo; }
    public void setImagem_residuo(ImagemResiduo imagem_residuo) { this.imagem_residuo = imagem_residuo; }

    public static class ImagemResiduo {
        private String type;
        private List<Integer> data;

        public String getNomeArquivo() {
            StringBuilder sb = new StringBuilder();
            for (int valor : data) {
                sb.append((char) valor);
            }
            return sb.toString();
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public List<Integer> getData() { return data; }
        public void setData(List<Integer> data) { this.data = data; }
    }
}
