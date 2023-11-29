/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triplethreat.coit13229.assignment2.webapp.service;

import com.triplethreat.coit13229.assignment2.webapp.Firetrucks;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
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

/**
 *
 * @author kfarc
 */
@Stateless
@Path("com.triplethreat.coit13229.assignment2.webapp.firetrucks")
public class FiretrucksFacadeREST extends AbstractFacade<Firetrucks> {

    @PersistenceContext(unitName = "com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public FiretrucksFacadeREST() {
        super(Firetrucks.class);
    }

    //create Firetruck
    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Firetrucks entity) {
        super.create(entity);
    }

    //edit Firetruck
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})    
    @Produces(MediaType.TEXT_PLAIN)
    public String edit(@PathParam("id") Integer id, String entity) throws SQLException {
        return super.edit(entity, Firetrucks.class);
    }

    //find Firetruck by id
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    //get all Firetrucks
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String findAll() {
        return super.findAll();
    }

    //get entity manager
    @Override
    protected EntityManager getEntityManager() {
        if (em == null) {
            em = Persistence.createEntityManagerFactory("com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU").createEntityManager();
        }
        return em;
    }
    
}
