package com.nami.webstore.model;

public class Slide {
    private String imagem;
    private String link;

    public Slide(String imagem, String link) {
        this.imagem = imagem;
        this.link = link;
    }

    public String getImagem() { return imagem; }
    public String getLink() { return link; }
}
