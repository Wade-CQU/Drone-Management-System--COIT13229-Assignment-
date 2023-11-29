module com.triplethreat.coit13229.ass2.clientgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    
    opens com.triplethreat.coit13229.ass2.clientgui to javafx.fxml;
    opens domain;
    exports com.triplethreat.coit13229.ass2.clientgui;
}
