/* COIT13229 - Assignment 1
 * Author: Ky Farcich
 * File: DroneClient.java 
 * Description: this script is responsible for handling the drone client.
 *  This involves listening for server requests, and sending information to the server.
 */
import domain.*;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.Random;

public class DroneClient {
    private Socket s = null;
    private int serverPort;
    private ObjectInputStream in;
    private ObjectOutputStream out;
  
    private Drone drone; // associated client's drone object.
    private PositionSender ps; // object responsible for 10 second location pinging.
    
    public static void main(String[] args) throws IOException {
        // create drone client and start client script:
        DroneClient client = new DroneClient();
    }
    
    // Constructor:
    public DroneClient() throws IOException{
        // create drone object for client (randomise start position):
        drone = new Drone(-1, "", new Random().nextInt(675), new Random().nextInt(475));
        drone.setOnline(true);
        
        // prompt user for drone details:
        Scanner scannerIn = new Scanner(System.in);
        
        while (drone.getName().equals("")) { // keep asking until a name is provided:
            System.out.println("Please enter this Drone's Name: ");
            drone.setName(scannerIn.nextLine().trim()); // set drone name (trimmed to avoid leading/trailing spaces).
        }
        
        while (drone.getId() == -1) { // keep asking until a drone ID is provided:
            System.out.println("Please enter this Drone's ID: ");
            try {
                drone.setId(Integer.parseInt(scannerIn.nextLine())); // set drone's id.
                if (drone.getId() < 0) { // check if id is positive:
                    throw new Exception();
                }
            }
            catch (Exception e) { // print message if number is not supplied or is not positive:
                System.out.println("Drone ID can only be a positive integer.");
            }
        }
        
        // connect to server:
        try {
            // create socket:
            serverPort = 6969;
            s = new Socket("localhost", serverPort); 
            System.out.println(s.getInetAddress());
            
            // create input/output streams:
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());
            System.out.println("out "+ out.getClass()+ " - in " + in.getClass());
            
            // sends registration to the server and await acknowledge:
            sendRegistration();
            receiveInstructions(); // start listening loop.
        } 
        catch(IOException e) {
            System.out.println("Connection Exception: " + e.getMessage());
        }
    }

    // attempts to send Drone Registration details to the server:
    public void sendRegistration() throws IOException {
        try {
            out.writeObject("Register"); // informs server it will receive a drone registration.
            out.writeObject(drone); // sends the registration.
        } 
        catch (Exception e) {
            System.err.println("Registration Exception:  " + e.getMessage());
        }   
    }

    // receive requests from server:
    public void receiveInstructions() {
        Object data;
        try{
            // listen to server:
            while (true){
                data = in.readObject(); // get server message.
                System.out.println("Instruction Received: " + data);
                // ensure String was received:
                if (data instanceof String) {
                    String message = (String) data;
                    // if registration was successful:
                    if (message.equalsIgnoreCase("Registered")) {
                        // start sending position every 10 seconds:
                        ps = new PositionSender(in, out, drone);
                    } // if movement was acknowledged:
                    else if (message.equalsIgnoreCase("Acknowledged Movement")) {
                        System.out.println("Movement Acknowledged.\n");
                        ps.acknowledged(false); // inform position sender that the last movement was acknowledged.
                    }
                    else if (message.equalsIgnoreCase("Out of Bounds")) 
                    { // if moved off map, return to prior position:
                        System.out.println("Drone moved out of bounds. Returning.\n");
                        ps.acknowledged(true); // revert movement.
                        out.writeObject("Drone Returned to Bounds");
                    } // if drone instructed to recall:
                    else if (message.equalsIgnoreCase("Recall")) {
                        ps.interrupt(); // stop location sending.
                        System.out.println("Drone returning to base.\n");
                        out.writeObject("Drone Returned"); // inform server that drone has returned.
                        break; // end client. 
                    } // if server wants to move this drone:
                    else if (message.equalsIgnoreCase("Move Drone")) {
                        Drone d = new Drone((Drone) in.readObject()); // get the new position for drone:
                        drone.setPositionX(d.getPositionX());
                        drone.setPositionY(d.getPositionY());

                        System.out.println("Drove moved by server to: (" + drone.getPositionX() + ", " + drone.getPositionY() + ")");
                        out.writeObject("Drone Moved"); // confirm to server that drone moved.
                        out.writeObject(new Drone(drone)); // send new drone details to server.
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Listening Exception: " + e.getMessage());
        } 
    }
}

class PositionSender extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Drone drone;

    private boolean acknowledged; // allows the 10 second loop to continue.
    private boolean revertPosition; // used to determine if previous movement was safe.
    
    //constructor:
    public PositionSender(ObjectInputStream in, ObjectOutputStream out, Drone drone) {
        this.in = in;
        this.out = out;
        this.drone = drone;
        start();
    }
        
    // used to simulate drone movement:
    public void moveDrove() {
        Random r = new Random(); // get random drone coordinates:
        drone.setPositionX(drone.getPositionX() + r.nextInt(100) - 50);
        drone.setPositionY(drone.getPositionY() + r.nextInt(100) - 50);
    }
    // used to simulate fire reporting:
    public FireData detectFire() {
        FireData fire = new FireData(drone.getPositionX(), drone.getPositionY(), new Random().nextInt(9), new Random().nextInt(90) + 10);
        return fire;
    }
    
    @Override // main thread loop:
    public void run() {
        while (!isInterrupted()) {
            try 
            { // send drone's position to the server:
                Thread.sleep(10000); // wait 10 seconds.

                int positionX = drone.getPositionX();
                int positionY = drone.getPositionY();
                moveDrove(); // get current position and move drone to new location.

                acknowledged = false; // reset booleans.
                revertPosition = false;
                
                out.writeObject("Drone Moved"); // alert server that drone moved.
                out.writeObject(new Drone(drone)); // send new drone details to server.
                System.out.println("Sent new Drone location: (" + drone.getPositionX() + ", " + drone.getPositionY() + ")");
                
                // await server acknowledgement:
                while (!acknowledged) 
                { // drone client will set 'acknowledged' back to true once server responds.
                    Thread.sleep(1000);
                }
                if (revertPosition) { // if server commanded drone to move back:
                    drone.setPositionX(positionX); // return to prior position.
                    drone.setPositionY(positionY);
                }
                else { // simulate drone finding a fire and reporting it:
                    if (new Random().nextInt(25) == 1) { // random chance to fire fire:
                        FireData fire = detectFire();
                        fire.setReportingDrone(drone.getId());
                        out.writeObject("Fire Detected");
                        out.writeObject(fire);  // send fire data to server.
                    }
                }
            } 
            catch (InterruptedException e) {
                break; // if thread is interrupted, end the loop.
            }
            catch (Exception e) {
                System.err.println("Pinging Exception: " + e.getMessage());
            }
        }
    }
    // used by the client to communicate to the position pinger when it's movement has been registed or rejected by the server:
    public void acknowledged(boolean revert) {
        acknowledged = true;
        revertPosition = revert;
    }
}