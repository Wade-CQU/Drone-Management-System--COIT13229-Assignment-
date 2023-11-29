/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;


/**
 *
 * @author kfarc
 */
public class Drone{

    private int id;
    private String name;
    private int xpos;
    private int ypos;

    public Drone() {
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

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    @Override
    public String toString() {
        return "Drone{" + "id=" + id + ", name=" + name + ", xpos=" + xpos + ", ypos=" + ypos + '}';
    }


    
}
