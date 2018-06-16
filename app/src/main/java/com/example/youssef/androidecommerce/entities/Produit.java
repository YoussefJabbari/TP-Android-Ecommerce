package com.example.youssef.androidecommerce.entities;

/**
 * Created by youssef on 02/05/2018.
 */

public class Produit {

    private Integer idproduit;
    private String nomproduit;
    private String typeproduit;
    private int prix;
    private int idcatalogue;

    public Integer getIdproduit() {
        return idproduit;
    }

    public String getNomproduit() {
        return nomproduit;
    }

    public String getTypeproduit() {
        return typeproduit;
    }

    public Produit(Integer idproduit, String nomproduit, String typeproduit, int prix, int idcatalogue) {
        this.idproduit = idproduit;
        this.nomproduit = nomproduit;
        this.typeproduit = typeproduit;
        this.prix = prix;
        this.idcatalogue = idcatalogue;
    }

    @Override
    public String toString() {
        return nomproduit;
    }

    public int getPrix() {
        return prix;
    }

    public int getIdcatalogue() {
        return idcatalogue;
    }

    public void setIdproduit(Integer idproduit) {
        this.idproduit = idproduit;
    }

    public void setNomproduit(String nomproduit) {
        this.nomproduit = nomproduit;
    }

    public void setTypeproduit(String typeproduit) {
        this.typeproduit = typeproduit;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public void setIdcatalogue(int idcatalogue) {
        this.idcatalogue = idcatalogue;
    }
}