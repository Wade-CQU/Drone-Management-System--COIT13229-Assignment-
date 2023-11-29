/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

public class Fire {

    private int id;
    private int isActive;
    private int intensity;
    private Long burningAreaRadius;
    private int xpos;
    private int ypos;

    public Fire() {
    }

    public Fire(int id, int isActive, int intensity, Long burningAreaRadius, int xpos, int ypos) {
        this.id = id;
        this.isActive = isActive;
        this.intensity = intensity;
        this.burningAreaRadius = burningAreaRadius;
        this.xpos = xpos;
        this.ypos = ypos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public Long getBurningAreaRadius() {
        return burningAreaRadius;
    }

    public void setBurningAreaRadius(Long burningAreaRadius) {
        this.burningAreaRadius = burningAreaRadius;
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
        return "Fire " + id + (isActive == 1 ? " (ACTIVE)" : " (Inactive)") + " - Location: (" + xpos + ", " + ypos + "), Intensity: " + intensity + ", Radius: " + burningAreaRadius + "m";
    }
    
   
    
}
