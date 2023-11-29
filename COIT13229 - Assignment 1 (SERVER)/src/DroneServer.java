/* COIT13229 - Assignment 1
 * Author: Ky Farcich
 * File: DroneServer.java 
 * Description: the main server script.
 *  Main contact for all drone clients. Manages connections, displays and stores data.
 */
import domain.*;
import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.sql.*;

public class DroneServer {
    private static LinkedList<Drone> drones;
    private static LinkedList<FireData> fires;    
    private static LinkedList<FireTruck> trucks;
    private static LinkedList<Connection> connections = new LinkedList<>();
  
    static ServerSocket serverSocket;
    final static int SERVER_PORT = 6969;
    static Socket clientSocket;
    static java.sql.Connection dbConnection;
    
    public final static int MAP_LENGTH_X = 675;
    public final static int MAP_LENGTH_Y = 475;
    private static ServerGUI gui;
    private static boolean running;
    
    // constructor:
    public DroneServer() throws IOException, SQLException {
        // instantiate drone list and file manager:
        drones = new LinkedList<>();
        fires = new LinkedList<>();
        
        // Get the connection from the NetBeans database connection pool:
        dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ibdms_server", "netbeaner", "1234567890");

        // get existing drones from database:
        drones = getDronesFromDB();
        if (drones == null) { // ensure drone list not null:
            drones = new LinkedList<>();
        }
        // get existing fires from database:
        fires = getFiresFromDB();
        if (fires == null) { // ensure fire list not null:
            fires = new LinkedList<>();
        }
        // get existing trucks from database:
        trucks = getTrucksFromDB();
        if (trucks == null) { // ensure fire list not null:
            trucks = new LinkedList<>();
        }
        
        System.out.println("Initial Drones: " + drones);        
        System.out.println("Initial Fires: " + fires);        
        System.out.println("Initial Trucks: " + trucks);
        
        // configure server socket:
        System.setProperty("java.net.preferIPv4Stack" , "true");     
        serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println(serverSocket.getInetAddress());
        
        // create the gui:
        gui = new ServerGUI(this, drones, fires, trucks);
    }
    
    // creates a new connection thread for each client connecting:
    public void createClientThread() throws IOException {
        System.out.println("Server waiting for drone requests...");  
 
        while(running) {
            try {
            // wait for a drone connection and accept it:
            clientSocket = serverSocket.accept();
            
            // create a new connection to the drone:
            System.out.println("New Connection: " + clientSocket.getInetAddress());	
            Connection c = new Connection(this, clientSocket, drones, fires);
            connections.add(c); // add this connection to the list.
            }
            catch (Exception e) {
                running = false; // stop running if exception occurs.
                System.out.println("Connection Manager Exception: " + e.getMessage());
            }
        }
    }
    // moves target drone to new location:
    public void moveDrone(int id, int x, int y) {
        System.out.println("Moving drone " + id + "...");
        // check each connection to find desired drone:
        for (Connection c : connections) {
            if (c.getDroneId() == id) {
                c.moveDrone(x,y); // move drone.
            }
        }
    }
    // deletes fire data from records:
    public void removeFire(int id) {
        // find and remove fire data from list:
        ListIterator<FireData> iterator = fires.listIterator();
        while (iterator.hasNext()) {
            FireData d = iterator.next();
            if (d.getId() == id) {
                disableFireInDB(d);
                System.out.println("Fire " + id + " set to no longer active in database.");
                iterator.remove();
                break;
            }
        }
    }
    // recalls all drones to base:
    public void recallDrones() {
        System.out.println("Recalling drones...");
        try {
            // instruct all drones to return and close all connections:
            for (Connection c : connections) {
                c.interrupt();
            }
            // wait until all connections are terminated:
            while (connections.size() > 0) {
                Thread.sleep(1000); // wait until all connections are closed.
            }
        }
        catch (Exception e) {
            System.out.println("Recall Exception: " + e.getMessage());
        }
        System.out.println("All drones returned to base.");
        
        //set drones to inactive for file storing:
        for (Drone d : drones) {
            d.setOnline(false);
        }
    }
    // shuts down the server and saves changes:
    public void shutDown() {
        try {
            // recall drones:
            recallDrones(); 
            
            // save changes:
            int count = saveDronesToDB(drones);
            System.out.println(count + " Drones saved to Database.");
            count = saveFiresToDB(fires);
            System.out.println(count + " Fires saved to Database.");
                    
            dbConnection.close();
            // close server:
            running = false; 
            System.out.println("Shutting down...");
            serverSocket.close();
            gui.dispose();
        }
        catch (Exception e) {
            System.out.println("Shutdown Exception: " + e.getMessage());
        }
    }
    // removes connection object from listed connections:
    public void removeConnection(Connection c) {
        connections.remove(c);
    }
  
    // Get drones from database:
    public LinkedList<Drone> getDronesFromDB() {
        LinkedList<Drone> drones = new LinkedList<>();
        try {
            // Send the SELECT query:
            String query = "SELECT * FROM drone";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Obtain the results:
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int x = rs.getInt("xPos");                
                int y = rs.getInt("yPos");
                drones.add(new Drone(id,name,x,y));
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return null;
        }
        return drones;
    }
    public int saveDronesToDB(LinkedList<Drone> drones) {
        try {
            int count = 0;
            
            // Perform Update Queries:
            for (Drone d : drones) {
                String query = "UPDATE drone SET name = '" + d.getName() + "', xPos = " + d.getPositionX() 
                        + ", yPos = " + d.getPositionY() + " WHERE id = " + d.getId();
                Statement stmt = dbConnection.createStatement();
                int rowsAffected = stmt.executeUpdate(query);
                if (rowsAffected > 0) { // if query successful, increment counter:
                    count++;
                }
            }
            
            return count;
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return 0;
        }
    }
    public boolean addDroneToDB(Drone d) {
        try {
            // Perform Insert Query:
            String query = "INSERT INTO drone VALUES(" + d.getId() + ", '" + d.getName() + "', " + d.getPositionX() 
                           + ", " + d.getPositionY() + ");";
            Statement stmt = dbConnection.createStatement();
            int rowsAffected = stmt.executeUpdate(query);
            if (rowsAffected > 0) { // if query successful:
                return true;
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return false;
    }
    // Get fires from database:
    public LinkedList<FireData> getFiresFromDB() {
        LinkedList<FireData> fire = new LinkedList<>();
        try {
            // Send the SELECT query:
            String query = "SELECT * FROM fire WHERE isActive = 1";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Obtain the results:
            while (rs.next()) {
                int id = rs.getInt("id");
                int intensity = rs.getInt("intensity");
                int x = rs.getInt("xpos");                
                int y = rs.getInt("ypos");
                float radius = rs.getFloat("burningAreaRadius");
                FireData f = new FireData(x,y, intensity,radius);
                f.setId(id);
                fire.add(f);
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return null;
        }
        return fire;
    }
    public int saveFiresToDB(LinkedList<FireData> fires) {
        try {
            int count = 0;
            
            // Perform Update Queries:
            for (FireData d : fires) {
                String query = "UPDATE fire SET intensity = " + d.getSeverity() + ", xPos = " + d.getPositionX() 
                        + ", yPos = " + d.getPositionY() + ", burningAreaRadius = " + d.getRadius() + " WHERE id = " + d.getId();
                Statement stmt = dbConnection.createStatement();
                int rowsAffected = stmt.executeUpdate(query);
                if (rowsAffected > 0) { // if query successful, increment counter:
                    count++;
                }
            }
            return count;
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return 0;
        }
    }
    public boolean addFireToDB(FireData d) {
        try {
            // Perform Insert Query:
            System.out.println("Radius: " + d.getRadius());
            String query = "INSERT INTO fire VALUES(" + d.getId() + ", 1, " + d.getSeverity() + ", " + d.getRadius() + ", " + d.getPositionX() 
                           + ", " + d.getPositionY() + ");";
            Statement stmt = dbConnection.createStatement();
            int rowsAffected = stmt.executeUpdate(query);
            if (rowsAffected > 0) { // if query successful:
                return true;
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return false;
    }
    public boolean disableFireInDB(FireData d) {
        try {
            // Perform Insert Query:
            String query = "UPDATE fire SET isActive = 0 WHERE id = " + d.getId() + ";";
            Statement stmt = dbConnection.createStatement();
            int rowsAffected = stmt.executeUpdate(query);
            if (rowsAffected > 0) { // if query successful:
                return true;
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return false;
    }
    public int getCurrentID() {
        try {
            // Send the SELECT query:
            String query = "SELECT MAX(id) as ID FROM fire";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Obtain the results:
            while (rs.next()) {
                return rs.getInt("ID");
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
        return -1;
    }
    // Get trucks from database:
    public LinkedList<FireTruck> getTrucksFromDB() {
        LinkedList<FireTruck> truck = new LinkedList<>();
        try {
            // Send the SELECT query:
            String query = "SELECT * FROM firetrucks WHERE NOT ISNULL(designatedFireId)";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Obtain the results:
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");                
                int fire = rs.getInt("designatedFireId");
                
                truck.add(new FireTruck(id, name, fire));
            }
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            return null;
        }
        return truck;
    }
    // Main Method:
    public static void main (String args[]) {
        try {
            // creates the drone server and starts the main thread:
            DroneServer server = new DroneServer();
            running = true;
            server.createClientThread(); // starts listening for client connections.
        }
        catch (IOException e) {
            System.out.println("Startup Exception: " + e.getMessage());
        }
        catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }
}

// Connection class to manage multiple drone client connections:
class Connection extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    DroneServer server;
    private int droneId;
    private String droneName;
    
    private LinkedList<Drone> droneList;
    private LinkedList<FireData> fireList;
    
    // constructor:
    public Connection (DroneServer server, Socket aClientSocket, LinkedList<Drone> drones, LinkedList<FireData> fires) {
        // gets values:
        this.server = server;
        droneList = drones; 
        fireList = fires;
        // tries to establish output and input to client:
        try {
            clientSocket = aClientSocket;
     
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            // start the thread for this connection:
            this.start();
            System.out.println("Thread Started: " + in.getClass());
        } 
        catch(IOException e) {
            System.out.println("Connection Start Exception: " + e.getMessage());
        }
    }

    @Override
    public void run(){
        // Thread operations:
        try { 
            // read client inputs:
            while (!isInterrupted()){
                String option = (String) in.readObject();
                if (option.equalsIgnoreCase("Register")) // if client wants to register drone:
                { 
                    // get the sent drone:
                    Drone data = (Drone) in.readObject();
                    data.setOnline(true);
                    Drone clientDrone = new Drone(data);
                    System.out.println("\n Drone " + clientDrone.getId() + " seeking registration.\n");
                       
                    //check if drone already exists:
                    boolean exists = false;
                    for (Drone d : droneList) {
                        if (d.getId() == clientDrone.getId()) {
                            exists = true;
                            break;
                        }
                    }
                        
                    // if drone exists, update it in the list, else add it to the list:
                    if (exists) {
                        UpdateDroneInList(clientDrone);   
                        System.out.println("Drone updated in list.");
                    }
                    else {
                        droneList.add(clientDrone);
                        server.addDroneToDB(clientDrone);
                        System.out.println("Successfully registered.");
                    }
                        
                    droneId = clientDrone.getId();
                    droneName = clientDrone.getName();
                    int count = server.saveDronesToDB(droneList);
                    System.out.println(count + " Drones saved to Database.");
                    out.writeObject("Registered"); // inform client that they've been registed.
                } 
                else if (option.equalsIgnoreCase("Drone Moved"))
                { // if the drone sends a position update, get the new position:
                    Drone drone = new Drone((Drone) in.readObject());
                        
                    System.out.println("Drone " + drone.getId() + ": Moved to location (" 
                                       + drone.getPositionX() + ", " + drone.getPositionY() + ")");
                    
                    String response = "Acknowledged Movement";
                    // if position exceeds bounds, warn Drone: 
                    if (drone.getPositionX() < 0 || drone.getPositionX() > DroneServer.MAP_LENGTH_X || drone.getPositionY() < 0 || drone.getPositionY() > DroneServer.MAP_LENGTH_Y) {
                        response = "Out of Bounds";
                    }
                    else { // if drone movement within bounds:
                        UpdateDroneInList(drone); // update in list.
                    }
                    
                    // send response:
                    out.writeObject(response);
                    
                    //await acknowledgement if drone out of bounds:
                    if (response.equalsIgnoreCase("Out of Bounds")) {
                        String message = (String) in.readObject();
                        if (message.equalsIgnoreCase("Drone Returned to Bounds")) {
                            System.out.println("Drone " + drone.getId() + " returned to the boundaries of the map.");
                        }
                    }
                }
                else if (option.equalsIgnoreCase("Fire Detected")) { // if fire detected by drone:
                    FireData fire = (FireData) in.readObject();
                    fire.setId(server.getCurrentID() + 1); // get fire data, give it an identifier.
                    System.out.println(fire);
                    fireList.add(fire); // save fire data.
                    server.addFireToDB(fire);
                }
            }
        }
        catch(Exception e) {
            System.out.println("Connection Thread Exception: " + e.getMessage());
        } 
        CloseConnection();
    }
    // used to tell the drone to move:
    public void moveDrone(int x, int y) {
        try {
            out.writeObject("Move Drone"); // tell drone to move.
            out.writeObject(new Drone(droneId, droneName, x, y)); // give drone movement data.
        }
        catch (Exception e) {
            System.out.println("Movement Exception: " + e.getMessage());
        }
    }
    // used to update drone data in server list:
    private void UpdateDroneInList(Drone clientDrone) {
        ListIterator<Drone> iterator = droneList.listIterator();
        while (iterator.hasNext()) { // iterate through list until id matches:
            Drone d = iterator.next();
            if (d.getId() == clientDrone.getId()) {
                d.updateDrone(clientDrone.getName(), clientDrone.getPositionX(), clientDrone.getPositionY(), clientDrone.isOnline());
                iterator.set(d); // update drone ^.
                break;
            }
        }
    }
    // terminate connection to drone client and recall it to base:
    public void CloseConnection() {
        this.interrupt(); // interupt own thread.
        try {
            out.writeObject("Recall"); // get drone to return to base.
            String s = (String) in.readObject(); // wait for acknowledgment.
            System.out.println(s);
            server.removeConnection(this); // terminate connection and remove from server list.
        }
        catch (Exception e) {
            System.out.println("Connection Closing Exception: " + e.getMessage());
        }
    }
    // get id of this connection's drone:
    public int getDroneId() {
        return droneId;
    }
}
