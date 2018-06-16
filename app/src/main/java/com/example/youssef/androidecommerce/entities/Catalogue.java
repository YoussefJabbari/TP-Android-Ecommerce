package com.example.youssef.androidecommerce.entities;

/**
 * Created by youssef on 02/05/2018.
 */

import java.util.Date;


public class Catalogue {


    private Integer idcatalogue;
    private String nomcatalogue;
    private Date datecatalogue;

    public Integer getIdcatalogue() {
        return idcatalogue;
    }

    public String getNomcatalogue() {
        return nomcatalogue;
    }

    public Date getDatecatalogue() {
        return datecatalogue;
    }

    public Catalogue(Integer idcatalogue, String nomcatalogue, Date datecatalogue) {
        this.idcatalogue = idcatalogue;
        this.nomcatalogue = nomcatalogue;
        this.datecatalogue = datecatalogue;
    }

    public void setIdcatalogue(Integer idcatalogue) {
        this.idcatalogue = idcatalogue;
    }

    public void setNomcatalogue(String nomcatalogue) {
        this.nomcatalogue = nomcatalogue;
    }

    @Override
    public String toString() {
        return nomcatalogue;
    }

    public void setDatecatalogue(Date datecatalogue) {
        this.datecatalogue = datecatalogue;
    }


}