package com.triplethreat.coit13229.ass2.clientgui;

import domain.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.net.http.HttpRequest.BodyPublishers;
import javafx.scene.control.TextArea;

public class PrimaryController {

    private final String FIRE_URI = "http://localhost:8080/COIT13229-Assignment2-WebApp-1.0-SNAPSHOT/webresources/com.triplethreat.coit13229.assignment2.webapp.fire";
    private final String TRUCK_URI = "http://localhost:8080/COIT13229-Assignment2-WebApp-1.0-SNAPSHOT/webresources/com.triplethreat.coit13229.assignment2.webapp.firetrucks";
    private HttpClient httpClient;
    private Gson gson;
    
    private ArrayList<Fire> fireList;    
    private ArrayList<Firetrucks> truckList;

    public void initialize() throws IOException, InterruptedException {
        // prepare web app communication and conversion variables:
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        
        // update lists:
        refreshFireList(null);        
        refreshTruckList(null);
    }
    
    @FXML
    private TextArea fireMessageOutput;

    @FXML
    private TextArea truckMessageOutput;
    
    @FXML
    private ListView<Fire> fireListView;

    @FXML
    private TextField truckIdInput;

    @FXML
    private ListView<Firetrucks> truckListView;

    @FXML
    void assignTruckToFire(ActionEvent event) throws IOException, InterruptedException {
        Fire f = fireListView.getSelectionModel().getSelectedItem();
        if (truckIdInput.getText().equals("")) { // if no truck is specified:
            fireMessageOutput.setText("No Truck Specified. Please specify a truck by their number or name.");
            return;
        }
        Firetrucks t = getExistingTruck(truckIdInput.getText());
        if (t == null) { // if specified truck doesn't exist:
            fireMessageOutput.setText("The specified Truck does not exist. Please ensure the correct identifier number is used.");
            return;
        } else if (f == null) {// If fire is null, show an error message:
            fireMessageOutput.setText("No fire selected. Please select a Fire to move the Truck to.");
            return;
        } else { // otherwise, good to go:
            fireMessageOutput.setText("");
        }
        
        // update truck:
        t.setDesignatedFireId(f.getId());
        
        // Send to Web App:
        String editedTruck = gson.toJson(t);
        HttpRequest truckRequest = HttpRequest.newBuilder().uri(URI.create(TRUCK_URI + "/" + f.getId()))
        .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(editedTruck)).build(); 
        HttpResponse<String> editResponse = httpClient.send(truckRequest, BodyHandlers.ofString());
        
        // refresh trucks and input:
        truckIdInput.setText("");
        refreshTruckList(null);
    }

    @FXML
    void recallTruckFromFire(ActionEvent event) throws IOException, InterruptedException {
        Firetrucks t = truckListView.getSelectionModel().getSelectedItem();
        if (t == null) {
            truckMessageOutput.setText("No truck selected to recall. Please select a Truck from the list.");
            return;
        } else if (t.getDesignatedFireId() == 0) {
            truckMessageOutput.setText("Truck already has no Fire associations.");
            return;
        }
        else {
            truckMessageOutput.setText("");
        }
        
        // update truck:        
        t.setDesignatedFireId(0);
        
        // Send to Web App:
        String editedTruck = gson.toJson(t);
        HttpRequest truckRequest = HttpRequest.newBuilder().uri(URI.create(TRUCK_URI + "/" + t.getId()))
        .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(editedTruck)).build(); 
        HttpResponse<String> editResponse = httpClient.send(truckRequest, BodyHandlers.ofString());
        
        // refresh trucks:
        refreshTruckList(null);
    }

    @FXML
    void toggleFireActivity(ActionEvent event) throws IOException, InterruptedException {
        Fire f = fireListView.getSelectionModel().getSelectedItem();
        if (f == null) {
            fireMessageOutput.setText("No Fire selected to toggle activity. Please select a Fire from the list.");
            return;
        } else {
            fireMessageOutput.setText("");
        }
        // toggle fire's activity:
        f.setIsActive(f.getIsActive() == 0 ? 1 : 0);
        
        // Send to Web App:
        String editedFire = gson.toJson(f);
        HttpRequest fireRequest = HttpRequest.newBuilder().uri(URI.create(FIRE_URI + "/" + f.getId()))
        .header("Content-Type", "application/json").PUT(BodyPublishers.ofString(editedFire)).build(); 
        HttpResponse<String> editResponse = httpClient.send(fireRequest, BodyHandlers.ofString());
        
        // refresh fires:
        refreshFireList(null);
    }
    
    @FXML
    void refreshFireList(ActionEvent event) throws IOException, InterruptedException {
        // get items from web app:
        HttpRequest getRequestFire = HttpRequest.newBuilder().uri(URI.create(FIRE_URI)).GET().build();
        HttpResponse<String> getResponse = httpClient.send(getRequestFire, BodyHandlers.ofString());
        
        // turn objects from JSON format to the Java object list:
        java.lang.reflect.Type listType = new TypeToken<ArrayList<Fire>>(){}.getType();
        fireList = gson.fromJson(getResponse.body(), listType);
        
        // update list view:
        fireListView.getItems().clear();
        fireListView.getItems().addAll(fireList);
    }

    @FXML
    void refreshTruckList(ActionEvent event) throws IOException, InterruptedException {
        // get items from web app:
        HttpRequest getRequestFire = HttpRequest.newBuilder().uri(URI.create(TRUCK_URI)).GET().build();
        HttpResponse<String> getResponse = httpClient.send(getRequestFire, BodyHandlers.ofString());
        
        // turn objects from JSON format to the Java object list:
        java.lang.reflect.Type listType = new TypeToken<ArrayList<Firetrucks>>(){}.getType();
        truckList = gson.fromJson(getResponse.body(), listType);
        
        // update list view:
        truckListView.getItems().clear();
        truckListView.getItems().addAll(truckList);
    }

    private Firetrucks getExistingTruck(String id) {
        for (Firetrucks f : truckList) { // if any truck has a matching id:
            if (id.equals(f.getId() + "")) {
                return f;
            }
        }
        return null;
    }   
}