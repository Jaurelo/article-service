package com.microservice.articlesservice.DTO;



public class ArticleDto {
        private String nom;
        private int marge;

    public ArticleDto() {
    }

    public ArticleDto(String nom, int marge) {
        this.nom = nom;
        this.marge =  marge;
    }


    public String getNom() {        return nom;    }

    public void setNom(String nom) {        this.nom = nom;    }

    public int getMarge() {        return marge;    }

    public void setMarge(int marge) {        this.marge = marge;    }
}
