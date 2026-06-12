module com.comptafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires com.google.gson;
    requires java.net.http;
    requires java.prefs;
    
    opens com.comptafx to javafx.fxml;
    opens com.comptafx.presentation to javafx.fxml;
    opens com.comptafx.entities to javafx.base;
    opens com.comptafx.ai to com.google.gson;
    
    exports com.comptafx;
    exports com.comptafx.presentation;
    exports com.comptafx.entities;
    exports com.comptafx.metier;
    exports com.comptafx.dao;
    exports com.comptafx.ai;
}
