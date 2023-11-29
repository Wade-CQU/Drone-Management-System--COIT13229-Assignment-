/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

public class Firetrucks {

    private int id;
    private String name;
    private int designatedFireId;

    public Firetrucks() {
    }

    public Firetrucks(int id, String name, int designatedFireId) {
        this.id = id;
        this.name = name;
        this.designatedFireId = designatedFireId;
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

    public int getDesignatedFireId() {
        return designatedFireId;
    }

    public void setDesignatedFireId(int designatedFireId) {
        this.designatedFireId = designatedFireId;
    }

    @Override
    public String toString() {
        return "Firetruck " + id + " - " + name + ", Designated Fire: " + designatedFireId + '.';
    }
}
