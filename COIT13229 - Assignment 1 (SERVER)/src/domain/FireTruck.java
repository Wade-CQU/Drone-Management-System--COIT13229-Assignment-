/* COIT13229 - Assignment 2
 * Author: Ky Farcich
 * File: FireTruck.java 
 * Description: Object used by the server for storing and displaying Fire Truck locations. May also be used in Client GUI application.
 */
package domain;

public class FireTruck {
    private int id;
    private String name;
    private int fireId;

    public FireTruck(int id, String name, int fireId) {
        this.id = id;
        this.name = name;
        this.fireId = fireId;
    }

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

    public int getFireId() {
        return fireId;
    }

    public void setFireId(int fireId) {
        this.fireId = fireId;
    }

    @Override
    public String toString() {
        return "Fire Truck ID: " + id + ", Name: " + name + ", FireId: " + fireId + '.';
    }
}
