/* COIT13229 - Assignment 1
 * Author: Ky Farcich
 * File: Drone.java 
 * Description: Drone object used by client and server.
 */
package domain;

import java.io.Serializable;

public class Drone implements Serializable {
    private int id;
    private String name;
    private int xPosition;    
    private int yPosition;
    private boolean online;
    
    // constructors:
    public Drone(int id, String name, int xPosition, int yPosition) {
        this.id = id;
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    public Drone (Drone duplicate) {
        this.id = duplicate.id;
        this.name = duplicate.name;
        this.xPosition = duplicate.xPosition;
        this.yPosition = duplicate.yPosition;
        this.online = duplicate.online;
    }
    
    // quick update function (for bulk updating):
    public void updateDrone(String name, int xPosition, int yPosition, boolean online) {
        this.name = name;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.online = online;
    }
    
    // getters and setters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPositionX() {
        return xPosition;
    }

    public void setPositionX(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getPositionY() {
        return yPosition;
    }

    public void setPositionY(int yPosition) {
        this.yPosition = yPosition;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    
    // to string:
    @Override
    public String toString() {
        return "\nDrone " + id + ": '" + name + "', Position = (" + xPosition + ", " + yPosition + ") - " + (online ? "Online" : "Offline");
    }
}