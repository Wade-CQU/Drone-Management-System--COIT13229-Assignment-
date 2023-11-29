/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triplethreat.coit13229.assignment2.webapp.service;

import com.triplethreat.coit13229.assignment2.webapp.Fire;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.gson.Gson;
import java.sql.SQLException;
import javax.persistence.Persistence;
/**
 *
 * @author kfarc
 */
@Stateless
@Path("com.triplethreat.coit13229.assignment2.webapp.fire")
public class FireFacadeREST extends AbstractFacade<Fire> {

    @PersistenceContext(unitName = "com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public FireFacadeREST() {
        super(Fire.class);
    }

    //create Fire
    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Fire entity) {
        super.create(entity);
    }

    //Update Fire
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})    
    @Produces(MediaType.TEXT_PLAIN)
    public String edit(@PathParam("id") Integer id, String entity) throws SQLException {
        return super.edit(entity, Fire.class);
    }

    //Find Fire by id
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    //Get all fires
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String findFire(){
        return super.findAll();
    }

    //Get enitity Manager
    @Override
    protected EntityManager getEntityManager() {
        if (em == null) {
            em = Persistence.createEntityManagerFactory("com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU").createEntityManager();
        }
        return em;
    }
    
}
