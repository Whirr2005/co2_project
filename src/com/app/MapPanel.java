package com.app;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

class MapPanel extends JPanel {

    // Constructor to set up the MapPanel
    public MapPanel() {
        // Initialize the JFXPanel which will host the WebView (browser component)
        JFXPanel jfxPanel = new JFXPanel();
        this.setLayout(new BorderLayout());
        this.add(jfxPanel, BorderLayout.CENTER); // Add JFXPanel to the MapPanel

        // Load the Google Maps or HTML content inside the JavaFX WebView
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            // Load the Google Maps URL or your custom HTML file here
            String googleMapsURL = "https://www.google.com/maps"; // Example URL
            webEngine.load(googleMapsURL);

            // Set the WebView in the JavaFX Scene
            Scene scene = new Scene(webView);
            jfxPanel.setScene(scene); // Set the JavaFX scene on the JFXPanel
        });
    }

    // Static method to create and show the frame with the map
    static void create() {
        JFrame newFrame = new JFrame("Map Plotter");
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(600, 400); // Frame size
        newFrame.setLocationRelativeTo(null); // Center the frame on the screen
        newFrame.setResizable(false);

        // Create an instance of the MapPanel and add it to the frame
        MapPanel mapPanel = new MapPanel();
        newFrame.add(mapPanel); // Add the MapPanel to the JFrame

        newFrame.setVisible(true); // Make the frame visible
    }
}

