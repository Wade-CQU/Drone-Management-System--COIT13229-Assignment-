/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triplethreat.coit13229.assignment2.webapp.service;

import com.triplethreat.coit13229.assignment2.webapp.Drone;
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
@Path("com.triplethreat.coit13229.assignment2.webapp.drone")
public class DroneFacadeREST extends AbstractFacade<Drone> {

    @PersistenceContext(unitName = "com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public DroneFacadeREST() {
        super(Drone.class);
    }

    //create Drone
    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Drone entity) {
        super.create(entity);
    }

    //Update Drone
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, String entity) throws SQLException {
        super.edit(entity, Drone.class);
    }

    //Find Drone by ID
    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    //Get all Drones
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String findAll() {
        return super.findAll();
    }

    //Get entity manager
    @Override
    protected EntityManager getEntityManager() {
        if (em == null) {
            em = Persistence.createEntityManagerFactory("com.triplethreat_COIT13229-Assignment2-WebApp_war_1.0-SNAPSHOTPU").createEntityManager();
        }
        return em;
    }
}
