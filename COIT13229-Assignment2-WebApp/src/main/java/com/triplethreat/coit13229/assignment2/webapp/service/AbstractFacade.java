/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triplethreat.coit13229.assignment2.webapp.service;

import com.google.gson.Gson;
import com.triplethreat.coit13229.assignment2.webapp.Fire;
import com.triplethreat.coit13229.assignment2.webapp.Firetrucks;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.*;
/**
 *
 * @author kfarc
 */
public abstract class AbstractFacade<T> {

    //instansiate  enitity class and Connection object
    private Class<T> entityClass;
    private static Connection dbConnection;
    
    //return entity class
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    //abstract method
    protected abstract EntityManager getEntityManager();

    //Pass in entity and persist to database
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    //edit existing enitity in database - take parameters: (JSON string for enitity, Class of the enitity)
    public String edit(String entity, Class<T> c) throws SQLException {
        
        //create JSON converter/reader object
        Gson gson = new Gson();
        //create a java object from the JSON string passes into parameter
        T object = gson.fromJson(entity, c);
        getEntityManager().merge(object);
        
        //update the entity in the database:
        //Create a new connection with the database
        dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ibdms_server", "root", "root");
        //Start query string
        String query = "UPDATE ";
        //If object is a Fire object
        if (object instanceof Fire) {
            //Finish query string to update a fire object
            query += "fire SET isActive = " + ((Fire) object).getIsActive() + " WHERE id = " + ((Fire) object).getId() + ";";
        //If object is a Firetruck object
        } else if (object instanceof Firetrucks) {
            //Finish query string to update a firetruck object
            query += "firetrucks SET designatedFireId = " + ((Firetrucks) object).getDesignatedFireId() + " WHERE id = " + ((Firetrucks) object).getId() + ";";
        }
        //create sql statement
        Statement stmt = dbConnection.createStatement();
        //execute sql query
        stmt.executeUpdate(query);
        //return the query generated
        return query;
    }

    //delete record
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    //find an object when passed the ID and return as JSON
    public String find(Object id) {
        //get reference to objects for the entity
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        //Find the entity with matching id
        T entity = getEntityManager().find(entityClass, id);
        //create JSON converter/reader object
        Gson gson = new Gson();
        //return object in form of JSON
        return gson.toJson(entity);
    }

    //find all and return as JSON array
    public String findAll() {
        //get reference to objects for the entity
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        //create a list object and fill with all records of the entity
        List<T> entityList = getEntityManager().createQuery(cq).getResultList();
        //create JSON converter/reader object
        Gson gson = new Gson();
        //create JSON array with all records from the entityList
        String entityListJson = gson.toJson(entityList);
        //return JSON Array
        return entityListJson;
    }
    
}
