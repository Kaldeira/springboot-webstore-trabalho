package com.nami.webstore.model;

public class Slide {
    private String imagem;
    private String imagemMobile;
    private String link;

    public Slide(String imagem, String imagemMobile, String link) {
        this.imagem = imagem;
        this.imagemMobile = imagemMobile;
        this.link = link;
    }

    public String getImagem() {
        return imagem;
    }

    public String getImagemMobile() {
        return imagemMobile;
    }

    public String getLink() {
        return link;
    }
}
