package main.java;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RFIDController {

    @FXML private TextField tagField;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;

    private final SerialService serialService = new SerialService(MainApp.properties.getProperty("rfid.default.port")); 
    
    @FXML
    private void handleScan() {
        String tagId = serialService.readTag();

        if (tagId == null || tagId.equals("NO TAG")) {
            showError("No tag detected!");
        } else {
            tagField.setText(tagId);
            showSuccess("Scanned: " + tagId);
        }
    }

    @FXML
    private void handleWrite() {
        String newId = tagField.getText();

        if (newId == null || newId.trim().isEmpty()) {
            showError("Please enter a tag ID to write.");
            return;
        }

        boolean success = serialService.writeTag(newId.trim());

        if (success) {
            showSuccess("Tag written successfully: " + newId);
        } else {
            showError("Failed to write tag. Check connection.");
        }
    }

    @FXML
    public void handleRefresh() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Clear everything?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Confirm Refresh");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                tagField.clear();
                statusLabel.setText("");
                statusLabel.setStyle("");
                logArea.clear();
               
            }
        });
    }



    private void appendLog(String logEntry) {
        String time = java.time.LocalTime.now().withNano(0).toString();
        logArea.appendText("[" + time + "] " + logEntry + "\n");
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red; -fx-background-color: #ffe6e6; -fx-border-color: red;");
        appendLog("ERROR: " + message);
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: green; -fx-background-color: #e8f5e9; -fx-border-color: green;");
        appendLog("INFO: " + message);
    }



}
