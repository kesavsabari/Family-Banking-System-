import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class ParentDashboardFX {

    private final ParentAcc parent;
    private final Bank bank = new Bank();

    public ParentDashboardFX(ParentAcc parent) {
        this.parent = parent;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Parent Dashboard");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setVgap(12);
        grid.setHgap(12);

        // ---------- TITLE ----------
        Label title = new Label("Welcome, " + parent.getName());
        title.getStyleClass().add("title-label");
        grid.add(title, 0, 0, 2, 1);

        // ---------- LINK CHILD ----------
        TextField linkChildField = new TextField();
        linkChildField.setPromptText("Child Username");

        Button linkButton = new Button("Link Child");

        grid.add(new Label("Link Child:"), 0, 1);
        grid.add(linkChildField, 1, 1);
        grid.add(linkButton, 1, 2);

        // ---------- DEPOSIT ----------
        TextField depositChildField = new TextField();
        depositChildField.setPromptText("Child Username");

        TextField depositAmountField = new TextField();
        depositAmountField.setPromptText("Amount");

        Button depositButton = new Button("Deposit");

        grid.add(new Label("Deposit to Child:"), 0, 3);
        grid.add(depositChildField, 1, 3);
        grid.add(depositAmountField, 1, 4);
        grid.add(depositButton, 1, 5);

        // ---------- VIEW CHILD BALANCE ----------
        TextField viewChildField = new TextField();
        viewChildField.setPromptText("Child Username");

        Button viewBalanceButton = new Button("View Balance");

        Label balanceResultLabel = new Label("");

        grid.add(new Label("View Child Balance:"), 0, 6);
        grid.add(viewChildField, 1, 6);
        grid.add(viewBalanceButton, 1, 7);
        grid.add(balanceResultLabel, 1, 8);

        // ---------- MONEY REQUESTS ----------
        TextArea requestArea = new TextArea();
        requestArea.setEditable(false);
        requestArea.setPrefHeight(120);

        TextField requestIdField = new TextField();
        requestIdField.setPromptText("Request ID");

        TextField approveChildField = new TextField();
        approveChildField.setPromptText("Child Username");

        TextField approveAmountField = new TextField();
        approveAmountField.setPromptText("Amount");

        Button loadRequestsButton = new Button("Load Requests");
        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");

        grid.add(new Label("Money Requests:"), 0, 9);
        grid.add(requestArea, 1, 9);

        grid.add(loadRequestsButton, 1, 10);

        grid.add(requestIdField, 1, 11);
        grid.add(approveChildField, 1, 12);
        grid.add(approveAmountField, 1, 13);

        grid.add(approveButton, 1, 14);
        grid.add(rejectButton, 1, 15);

        // ---------- STATUS ----------
        Label statusLabel = new Label("");
        grid.add(statusLabel, 0, 16, 2, 1);

        // ---------- LOGOUT ----------
        Button logoutButton = new Button("Logout");
        grid.add(logoutButton, 1, 17);

        // ================= LOGIC =================

        // Link child
        linkButton.setOnAction(e -> {
            boolean success = bank.linkChild(parent.getUsername(), linkChildField.getText());

            if (success) {
                statusLabel.setText("Child linked successfully.");
                statusLabel.getStyleClass().setAll("status-success");
                linkChildField.clear();
            } else {
                statusLabel.setText("Linking failed.");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        // Deposit
        depositButton.setOnAction(e -> {
            try {
                boolean success = bank.parentDeposit(
                        depositChildField.getText(),
                        Double.parseDouble(depositAmountField.getText())
                );

                if (success) {
                    statusLabel.setText("Deposit successful.");
                    statusLabel.getStyleClass().setAll("status-success");
                } else {
                    statusLabel.setText("Deposit failed.");
                    statusLabel.getStyleClass().setAll("status-error");
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid deposit input.");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        // View child balance
        viewBalanceButton.setOnAction(e -> {
            String childUser = viewChildField.getText();
            double balance = bank.getChildBalance(childUser);

            balanceResultLabel.setText("Balance: " + String.format("%.2f", balance));
        });

        // Load money requests
        loadRequestsButton.setOnAction(e -> {
            try {
                ResultSet rs = bank.getPendingRequests();
                requestArea.clear();

                while (rs.next()) {
                    requestArea.appendText(
                            "ID: " + rs.getInt("id") +
                                    " | Child: " + rs.getString("childUsername") +
                                    " | Amount: " + rs.getDouble("amount") + "\n"
                    );
                }
            } catch (Exception ex) {
                requestArea.setText("Failed to load requests.");
            }
        });

        // Approve request
        approveButton.setOnAction(e -> {
            try {
                boolean success = bank.approveRequest(
                        Integer.parseInt(requestIdField.getText()),
                        approveChildField.getText(),
                        Double.parseDouble(approveAmountField.getText())
                );

                statusLabel.setText(success ? "Request approved." : "Approval failed.");
                statusLabel.getStyleClass().setAll(success ? "status-success" : "status-error");

            } catch (Exception ex) {
                statusLabel.setText("Invalid approval input.");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        // Reject request
        rejectButton.setOnAction(e -> {
            try {
                boolean success = bank.rejectRequest(
                        Integer.parseInt(requestIdField.getText())
                );

                statusLabel.setText(success ? "Request rejected." : "Rejection failed.");
                statusLabel.getStyleClass().setAll(success ? "status-success" : "status-error");

            } catch (Exception ex) {
                statusLabel.setText("Invalid request ID.");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        logoutButton.setOnAction(e -> {
            stage.close();
            new LoginFX().start(new Stage());
        });

        Scene scene = new Scene(grid, 650, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
