/* COIT13229 - Assignment 1
 * Author: Ky Farcich
 * File: FireData.java 
 * Description: Object used by client and server to exchange fire data.
 */
package domain;
import java.io.Serializable;

public class FireData implements Serializable {
    private int id;
    private int xPosition;
    private int yPosition;
    private int reportingDrone;
    private int severity;
    private double radius;

    // constructor:
    public FireData(int xPosition, int yPosition, int severity, double radius) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.severity = severity;
        this.radius = radius;
    }

    // getters and setters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getReportingDrone() {
        return reportingDrone;
    }

    public void setReportingDrone(int reportingDrone) {
        this.reportingDrone = reportingDrone;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    // to string:
    @Override
    public String toString() {
        return "Fire Recorded at (" + xPosition + ", " + yPosition + "), by Drone " + reportingDrone + ", Severity Rating: " + severity + '.';
    }
}
