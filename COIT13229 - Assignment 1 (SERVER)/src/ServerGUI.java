/* COIT13229 - Assignment 1
 * Author: Ky Farcich
 * File: ServerGUI.java 
 * Description: responsible for the server's gui.
 */
import domain.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGUI extends JFrame {
    private DroneServer server;
    private LinkedList<Drone> drones;    
    private LinkedList<FireData> fires;    
    private LinkedList<FireTruck> trucks;
    private MapRefresher mapRefresher;
    
    private JPanel controlPanel;
    private JPanel mapPanel;
    private Image backgroundImage;
    private JButton moveDroneButton;
    private JButton removeFireButton;
    private JButton updateButton;
    private JButton recallDronesButton;
    private JButton shutdownButton;
    private JTextField droneIdInput;
    private JTextField xPositionInput;
    private JTextField yPositionInput;
    private JTextField fireIdInput;
    private JTextArea messageOutput;

    public ServerGUI(DroneServer server, LinkedList droneList, LinkedList fireList, LinkedList truckList) {
        this.server = server;
        fires = fireList; // link to server fire list.
        drones = droneList; // link to server drone list.
        trucks = truckList; // link to server truck list.
        
        // setup Frame:
        setTitle("Intelligent Bushfire Detection & Management System");
        setSize(900,540);
        setResizable(false);
        
        // when GUI closes, run server shutdown:
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownServer();
            }
        });
        
        // create panels:
        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(170, 500)); 
        mapPanel = new MapPanel(this);
        
        // Load elements and add to JPanel:
        JLabel idInputLabel = new JLabel("Drone ID to move:");
        droneIdInput = new JTextField(3);
        controlPanel.add(idInputLabel);        
        controlPanel.add(droneIdInput);
        
        JLabel locationLabel = new JLabel("Enter new Drone Location:");
        xPositionInput = new JTextField(3);
        yPositionInput = new JTextField(3);
        controlPanel.add(locationLabel);
        controlPanel.add(xPositionInput);
        controlPanel.add(yPositionInput);

        moveDroneButton = new JButton("Move Drone");
        moveDroneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { // when button pressed:
                //check inputs:
                String idInput = droneIdInput.getText().trim();
                String xInput = xPositionInput.getText().trim();
                String yInput = yPositionInput.getText().trim();
                // ensure inputs aren't blank:
                if (idInput.isEmpty() || xInput.isEmpty() || yInput.isEmpty()) {
                    messageOutput.setText("Please provide integers for Drone Movement.");
                    return;
                }
                try { // ensure inputs are integers:
                    int id = Integer.parseInt(idInput);
                    int x = Integer.parseInt(xInput);
                    int y = Integer.parseInt(yInput);
                    
                    // ensure coordinates are in range:
                    if (x > DroneServer.MAP_LENGTH_X || x < 0 || y > DroneServer.MAP_LENGTH_Y || y < 0) {
                        messageOutput.setText("Please ensure inputted location is within the bounds of the map.");
                        return; 
                    }
                    // get server to move drone client:
                    server.moveDrone(id, x, y);
                    messageOutput.setText("Drone Movement Request Processing...");
                }
                catch (Exception ex) {
                    messageOutput.setText("Please only provide numbers for the Drone Movement inputs.");
                }
            }
        });
        controlPanel.add(moveDroneButton);
        
        JLabel fireIdLabel = new JLabel("Enter ID of Fire to Delete:");
        fireIdInput = new JTextField(3);
        controlPanel.add(fireIdLabel);
        controlPanel.add(fireIdInput);
        
        removeFireButton = new JButton("Delete Fire Report");
        removeFireButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { // when pressed:
                // check input:
                String fireId = fireIdInput.getText().trim();
                if (fireId.isEmpty()) { // ensure input not empty.
                    messageOutput.setText("Please provide an integer to remove a fire report.");
                    return;
                }
                try {
                    // get integer from input:
                    int id = Integer.parseInt(fireId);
                    
                    server.removeFire(id); // get server to remove fire from system.
                    messageOutput.setText("Fire Removed.");
                }
                catch (Exception ex) {
                    messageOutput.setText("Please only provide the fire number to remove fire.");
                }
            }
        });
        controlPanel.add(removeFireButton);
        
        updateButton = new JButton("Update Map");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateGUI(); // update interface on button press.
                messageOutput.setText("Map Updated.");
            }
        });
        controlPanel.add(updateButton);
        
        JLabel messagesLabel = new JLabel("Server Messages:");
        controlPanel.add(messagesLabel);
        
        messageOutput = new JTextArea(10, 14); // configure output text area:
        messageOutput.setEditable(false); 
        messageOutput.setLineWrap(true);
        messageOutput.setWrapStyleWord(true);
        controlPanel.add(messageOutput);

        recallDronesButton = new JButton("Recall Drones");
        recallDronesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { // when pressed:
                server.recallDrones(); // get server to recall all drones.
                messageOutput.setText("Drones recalled.");
            }
        });
        controlPanel.add(recallDronesButton);
        
        shutdownButton = new JButton("Shutdown");
        shutdownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shutdownServer(); // shutdown on press.
            }
        });
        controlPanel.add(shutdownButton);
        
        // Load background image:
        backgroundImage = new ImageIcon("background.jpg").getImage();
        
        // build and show GUI:
        setLayout(new FlowLayout(FlowLayout.LEFT)); // aligns panels side by side.
        add(controlPanel);
        add(mapPanel);
        setVisible(true);
        
        // manage map refreshing at regular interval: 
        mapRefresher = new MapRefresher(this);
    }
    // updates the interface map:
    public void updateGUI() {
        mapPanel.repaint();
    }
    // stops the map refresher and requests server shutdown:
    public void shutdownServer() {
        messageOutput.setText("Recalling Drones and shutting down Server...");
        mapRefresher.interrupt();
        server.shutDown();
    }
    
    // getters:
    public LinkedList<Drone> getDrones() {
        return drones;
    }
    public LinkedList<FireData> getFires() {
        return fires;
    }
    public LinkedList<FireTruck> getTrucks() {
        return trucks;
    }
    public Image getBackgroundImage() {
        return backgroundImage;
    }
}
// map panel configuring:
class MapPanel extends JPanel {
    ServerGUI frame;
    
    // constructor:
    public MapPanel(ServerGUI frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(700, 500));// Set the preferred size of the panel
    }
    
    @Override // draws GUI map:
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(frame.getBackgroundImage(), 0, 0, null);
        
        // Draw fires if list is populated:      
        if (frame.getFires() != null) {
            for (FireData d : frame.getFires()) {
                String truckIds = "";
                for (FireTruck t : frame.getTrucks()) {
                    if (t.getFireId() == d.getId()) {
                        if (!truckIds.equals("")) {
                            truckIds += ",";
                        }
                        truckIds += t.getId();
                    }
                }
                String[] idArray = null;
                if (!truckIds.equals("")) {
                    idArray = truckIds.split(",");
                }
                drawFire(g, d.getId(), d.getPositionX(), d.getPositionY(), d.getRadius(), idArray); 
            }
        }
        
        // Draw drones if list is populated:
        if (frame.getDrones() != null) {
            for (Drone d : frame.getDrones()) {
                drawDrone(g, d.getId(), d.getPositionX(), d.getPositionY(), d.isOnline()); 
            }
        }
    }
    
    // Draws a drone at the desired coordinate (x,y):
    private static void drawDrone(Graphics g, int droneNumber, int x, int y, boolean online) {
        if (online) {
            g.setColor(Color.BLUE);
        }
        else {
            g.setColor(Color.GRAY);
        }
        g.fillOval(x, y, 20, 20);
        g.setColor(Color.darkGray);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Drone " + droneNumber, x - 10, y - 3);
    }
    // draws a fire at the desired coordinate:
    private static void drawFire(Graphics g, int fireNumber, int x, int y, double radius, String[] truckId) {
        g.setColor(Color.RED);
        g.fillOval(x, y, (int)radius, (int)radius);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Fire " + fireNumber,  x + ((int) radius / 2) - 26, y + ((int) radius / 2) + 7);
        
        if (truckId == null) {
            return;
        }
        
        int offset = 0;
        for (String i : truckId) {
            g.setColor(Color.MAGENTA);
            g.fillRect(x + ((int) radius / 2) - 30, y + (int) radius + offset, 60, 18);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,14));
            g.drawString("Truck " + i,  x + ((int) radius / 2) - 25, y + 13 + (int) radius + offset);
            
            offset += 20; // offset for if multiple trucks are at same fire.
        }
    }
} 
// object responsible for 10-second map updating:
class MapRefresher extends Thread {
    ServerGUI frame;
    
    // constructor:
    public MapRefresher(ServerGUI frame) {
        this.frame = frame;
        start();
    }
    
    @Override
    public void run() {
        while (!isInterrupted()) {
            try { // update gui every 10 seconds:
                Thread.sleep(10000);
                frame.updateGUI();
                System.out.println("GUI Refreshed.");
            }
            catch (InterruptedException e) {
                this.interrupt();
            }
            catch (Exception e) {
                System.out.println("Refresh Exception: " + e.getMessage());
            }
        }
    }
}
