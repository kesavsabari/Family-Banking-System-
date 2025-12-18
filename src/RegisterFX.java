import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterFX {

    private final Bank bank = new Bank();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Register User");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setVgap(15);
        grid.setHgap(15);

        Label title = new Label("Create New Account");
        title.getStyleClass().add("title-label");
        grid.add(title, 0, 0, 2, 1);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Parent", "Child");
        typeBox.setValue("Parent");

        grid.add(new Label("Account Type:"), 0, 1);
        grid.add(typeBox, 1, 1);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Label usernameStatusLabel = new Label("");

        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);
        grid.add(usernameStatusLabel, 1, 3);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        grid.add(new Label("Name:"), 0, 5);
        grid.add(nameField, 1, 5);

        TextField limitField = new TextField();
        limitField.setPromptText("Spending Limit");
        limitField.setDisable(true);

        grid.add(new Label("Spending Limit:"), 0, 6);
        grid.add(limitField, 1, 6);

        typeBox.setOnAction(e -> {
            if (typeBox.getValue().equals("Parent")) {
                limitField.setDisable(true);
                limitField.clear();
            } else {
                limitField.setDisable(false);
            }
        });

        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        grid.add(registerButton, 1, 7);
        grid.add(backButton, 1, 8);

        Label statusLabel = new Label("");
        grid.add(statusLabel, 0, 9, 2, 1);

        usernameField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                usernameStatusLabel.setText("");
                return;
            }

            if (bank.usernameExists(newText)) {
                usernameStatusLabel.setText("Username already taken.");
                usernameStatusLabel.getStyleClass().setAll("status-error");
            } else {
                usernameStatusLabel.setText("Username available.");
                usernameStatusLabel.getStyleClass().setAll("status-success");
            }
        });

        registerButton.setOnAction(e -> {

            String type = typeBox.getValue();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
                statusLabel.setText("All fields are required.");
                statusLabel.getStyleClass().setAll("status-error");
                return;
            }

            if (bank.usernameExists(username)) {
                statusLabel.setText("Username already exists.");
                statusLabel.getStyleClass().setAll("status-error");
                return;
            }

            boolean success;

            if (type.equals("Parent")) {
                success = bank.registerParent(username, password, name);
            } else {
                if (limitField.getText().isEmpty()) {
                    statusLabel.setText("Enter spending limit.");
                    statusLabel.getStyleClass().setAll("status-error");
                    return;
                }

                try {
                    double limit = Double.parseDouble(limitField.getText());
                    success = bank.registerChild(username, password, name, limit);
                } catch (NumberFormatException ex) {
                    statusLabel.setText("Invalid spending limit.");
                    statusLabel.getStyleClass().setAll("status-error");
                    return;
                }
            }

            if (success) {
                statusLabel.setText("Registration successful.");
                statusLabel.getStyleClass().setAll("status-success");
                usernameField.clear();
                passwordField.clear();
                nameField.clear();
                limitField.clear();
                usernameStatusLabel.setText("");
            } else {
                statusLabel.setText("Registration failed.");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        backButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, 500, 550);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
