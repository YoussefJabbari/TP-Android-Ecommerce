package com.example.youssef.androidecommerce.entities;

/**
 * Created by youssef on 14/05/2018.
 */

public class Livreur {

    private int idlivreur;
    private String loginlivreur;
    private String mdplivreur;
    private String nomlivreur;
    private String prenomlivreur;
    private String tellivreur;
    private String emaillivreur;

    public Livreur(int idlivreur, String loginlivreur, String mdplivreur, String nomlivreur, String prenomlivreur, String tellivreur, String emaillivreur) {
        this.idlivreur = idlivreur;
        this.loginlivreur = loginlivreur;
        this.mdplivreur = mdplivreur;
        this.nomlivreur = nomlivreur;
        this.prenomlivreur = prenomlivreur;
        this.tellivreur = tellivreur;
        this.emaillivreur = emaillivreur;
    }

    public Livreur(Livreur l) {
        this.idlivreur = l.getIdlivreur();
        this.loginlivreur = l.getLoginlivreur();
        this.mdplivreur = l.getMdplivreur();
        this.nomlivreur = l.getNomlivreur();
        this.prenomlivreur = l.getPrenomlivreur();
        this.tellivreur = l.getTellivreur();
        this.emaillivreur = l.getEmaillivreur();
    }

    public int getIdlivreur() {
        return idlivreur;
    }

    public void setIdlivreur(int idlivreur) {
        this.idlivreur = idlivreur;
    }

    public String getLoginlivreur() {
        return loginlivreur;
    }

    public void setLoginlivreur(String loginlivreur) {
        this.loginlivreur = loginlivreur;
    }

    public String getMdplivreur() {
        return mdplivreur;
    }

    public void setMdplivreur(String mdplivreur) {
        this.mdplivreur = mdplivreur;
    }

    public String getNomlivreur() {
        return nomlivreur;
    }

    public void setNomlivreur(String nomlivreur) {
        this.nomlivreur = nomlivreur;
    }

    public String getPrenomlivreur() {
        return prenomlivreur;
    }

    public void setPrenomlivreur(String prenomlivreur) {
        this.prenomlivreur = prenomlivreur;
    }

    public String getTellivreur() {
        return tellivreur;
    }

    public void setTellivreur(String tellivreur) {
        this.tellivreur = tellivreur;
    }

    public String getEmaillivreur() {
        return emaillivreur;
    }

    public void setEmaillivreur(String emaillivreur) {
        this.emaillivreur = emaillivreur;
    }

    @Override
    public String toString() {

        return nomlivreur + " " + prenomlivreur;
    }
}
