package com.example.youssef.androidecommerce.entities;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by youssef on 02/05/2018.
 */

public class Commande {

    private int idcommande;
    private String adresselivraison;
    private Date datecommande;
    private int commandevalidee;
    private int idlivreur;
    private int idclient;

    public Commande(int idcommande, String adresselivraison, Date datecommande, int commandevalidee, int idlivreur, int idclient) {
        this.idcommande = idcommande;
        this.adresselivraison = adresselivraison;
        this.datecommande = datecommande;
        this.commandevalidee = commandevalidee;
        this.idlivreur = idlivreur;
        this.idclient = idclient;
    }

    public Commande(Commande c) {
        this.idcommande = c.getIdcommande();
        this.adresselivraison = c.getAdresselivraison();
        this.datecommande = new Date(String.valueOf(c.getDatecommande()));
        this.commandevalidee = c.getCommandevalidee();
        this.idlivreur = c.getIdlivreur();
        this.idclient = c.getIdclient();
    }

    public int getIdcommande() {
        return idcommande;
    }

    public void setIdcommande(int idcommande) {
        this.idcommande = idcommande;
    }

    public String getAdresselivraison() {
        return adresselivraison;
    }

    public void setAdresselivraison(String adresselivraison) {
        this.adresselivraison = adresselivraison;
    }

    public Date getDatecommande() {
        return datecommande;
    }

    public void setDatecommande(Date datecommande) {
        this.datecommande = datecommande;
    }

    public int getCommandevalidee() {
        return commandevalidee;
    }

    public void setCommandevalidee(int commandevalidee) {
        this.commandevalidee = commandevalidee;
    }

    public int getIdlivreur() {
        return idlivreur;
    }

    public void setIdlivreur(int idlivreur) {
        this.idlivreur = idlivreur;
    }

    public int getIdclient() {
        return idclient;
    }

    public void setIdclient(int idclient) {
        this.idclient = idclient;
    }

    @Override
    public String toString() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return "Commande du " + sdf.format(datecommande) ;
    }
}