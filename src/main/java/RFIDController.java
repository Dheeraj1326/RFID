package main.java;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class RFIDController implements Initializable {
    
    @FXML private TextField tagField;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;
    
    private SerialService serialService;
    private Timeline hideMessageTimeline;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize serial service with error handling
            String portName = MainApp.properties.getProperty("rfid.default.port", "COM1");
            serialService = new SerialService(portName);
            
            // Set max character limit for tag field
            setTextFieldMaxLength(tagField, 
                Integer.parseInt(MainApp.properties.getProperty("rfid.tag.max.length", "20"))
            );
            
            // Initial log entry
            appendLog("RFID Controller initialized on port: " + portName);
            showInfo("System ready. Port: " + portName);
            
            tagField.setTextFormatter(new TextFormatter<>(change -> {
                change.setText(change.getText().toUpperCase());
                return change;
            }));
            
        } catch (Exception e) {
            showError("Failed to initialize serial connection: " + e.getMessage());
            appendLog("INIT ERROR: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleScan() {
        try {
            clearMessage(); // Clear any previous messages
            showInfo("Scanning for RFID tag...");
            
            String tagId = serialService.readTag();
            
            if (tagId == null || tagId.equals("NO TAG") || tagId.trim().isEmpty()) {
                showWarning("No tag detected! Please place a tag near the reader.");
            } else {
                tagField.setText(tagId.trim());
                showSuccess("Tag scanned successfully: " + tagId.trim());
            }
            
        } catch (Exception e) {
            showError("Scan failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleWrite() {
        try {
            final String newId = tagField.getText();
            
            // Input validation
            if (newId == null || newId.trim().isEmpty()) {
                showWarning("Please enter a tag ID to write.");
                tagField.requestFocus();
                return;
            }
            
            
            // Confirmation dialog for write operation
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Write Operation");
            confirmAlert.setHeaderText("Write Tag ID: " + newId);
            confirmAlert.setContentText("Are you sure you want to write this ID to the tag?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    performWrite(newId);
                }
            });
            
        } catch (Exception e) {
            showError("Write operation failed: " + e.getMessage());
        }
    }
    
    private void performWrite(String tagId) {
        try {
            showInfo("Writing tag ID: " + tagId + "...");
            
            boolean success = serialService.writeTag(tagId);
            
            if (success) {
                showSuccess("Tag written successfully: " + tagId);
            } else {
                showError("Failed to write tag. Please check connection and try again.");
            }
            
        } catch (Exception e) {
            showError("Write failed: " + e.getMessage());
        }
    }
    
    @FXML
    public void handleRefresh() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                               "This will clear all fields and logs. Continue?", 
                               ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Confirm Refresh");
        alert.setTitle("Clear All Data");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                tagField.clear();
                clearMessage();
                logArea.clear();
                appendLog("System refreshed by user");
                showInfo("System refreshed successfully");
            }
        });
    }
    
    // Message display methods
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: #ffe6e6; " +
                           "-fx-padding: 8px; -fx-background-radius: 5px; " +
                           "-fx-border-color: #ff9999; -fx-border-width: 1px; -fx-border-radius: 5px;");
        appendLog("ERROR: " + message);
        autoHideMessage(6000); // Hide after 6 seconds
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #e8f5e8; " +
                           "-fx-padding: 8px; -fx-background-radius: 5px; " +
                           "-fx-border-color: #27ae60; -fx-border-width: 1px; -fx-border-radius: 5px;");
        appendLog("SUCCESS: " + message);
        autoHideMessage(4000); // Hide after 4 seconds
    }
    
    private void showWarning(String message) {
        statusLabel.setText("⚠️ " + message);
        statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-background-color: #fef9e7; " +
                           "-fx-padding: 8px; -fx-background-radius: 5px; " +
                           "-fx-border-color: #f39c12; -fx-border-width: 1px; -fx-border-radius: 5px;");
        appendLog("WARNING: " + message);
        autoHideMessage(5000); // Hide after 5 seconds
    }
    
    private void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setStyle("-fx-text-fill: #3498db; -fx-background-color: #e8f4fd; " +
                           "-fx-padding: 8px; -fx-background-radius: 5px; " +
                           "-fx-border-color: #3498db; -fx-border-width: 1px; -fx-border-radius: 5px;");
        appendLog("INFO: " + message);
        autoHideMessage(3000); // Hide after 3 seconds
    }
    
    private void clearMessage() {
        if (hideMessageTimeline != null) {
            hideMessageTimeline.stop();
        }
        statusLabel.setText("");
        statusLabel.setStyle("");
    }
    
    private void autoHideMessage(int delayMillis) {
        if (hideMessageTimeline != null) {
            hideMessageTimeline.stop();
        }
        
        hideMessageTimeline = new Timeline(
            new KeyFrame(Duration.millis(delayMillis), e -> {
                statusLabel.setText("");
                statusLabel.setStyle("");
            })
        );
        hideMessageTimeline.play();
    }
    
    private void appendLog(String logEntry) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Platform.runLater(() -> {
            logArea.appendText("[" + time + "] " + logEntry + "\n");
            // Auto-scroll to bottom
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    private void setTextFieldMaxLength(TextField textField, int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
    
    // Cleanup method - call this when application closes
    public void cleanup() {
        try {
            if (serialService != null) {
                serialService.closePort();
                appendLog("Serial connection closed");
            }
            if (hideMessageTimeline != null) {
                hideMessageTimeline.stop();
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
    
    // Getter for serial service (for testing or external access)
    public SerialService getSerialService() {
        return serialService;
    }
}
