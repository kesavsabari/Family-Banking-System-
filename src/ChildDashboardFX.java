import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChildDashboardFX {

    private final ChildAcc child;
    private final Bank bank = new Bank();

    public ChildDashboardFX(ChildAcc child) {
        this.child = child;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Child Dashboard");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setVgap(15);
        grid.setHgap(15);

        Label title = new Label("Welcome, " + child.getName());
        title.getStyleClass().add("title-label");

        Label balanceLabel = new Label("Balance:");
        Label balanceValue = new Label("0.00");

        Button refresh = new Button("Refresh");

        TextField spendField = new TextField();
        spendField.setPromptText("Spend Amount");

        Button spendButton = new Button("Spend");

        // ✅ REQUEST MONEY
        TextField requestField = new TextField();
        requestField.setPromptText("Request Amount");

        Button requestButton = new Button("Request Money");

        Label statusLabel = new Label("");

        Button logout = new Button("Logout");

        grid.add(title, 0, 0, 2, 1);
        grid.add(balanceLabel, 0, 1);
        grid.add(balanceValue, 1, 1);
        grid.add(refresh, 1, 2);

        grid.add(new Label("Spend:"), 0, 3);
        grid.add(spendField, 1, 3);
        grid.add(spendButton, 1, 4);

        grid.add(new Label("Request Money:"), 0, 5);
        grid.add(requestField, 1, 5);
        grid.add(requestButton, 1, 6);

        grid.add(statusLabel, 0, 7, 2, 1);
        grid.add(logout, 1, 8);

        updateBalance(balanceValue);

        refresh.setOnAction(e -> updateBalance(balanceValue));

        spendButton.setOnAction(e -> {
            try {
                boolean success = bank.childSpend(
                        child.getUsername(),
                        Double.parseDouble(spendField.getText())
                );

                if (success) {
                    statusLabel.setText("Spend successful");
                    statusLabel.getStyleClass().setAll("status-success");
                    updateBalance(balanceValue);
                } else {
                    statusLabel.setText("Spend failed");
                    statusLabel.getStyleClass().setAll("status-error");
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid amount");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        // ✅ REQUEST MONEY LOGIC
        requestButton.setOnAction(e -> {
            try {
                boolean success = bank.requestMoney(
                        child.getUsername(),
                        Double.parseDouble(requestField.getText())
                );

                if (success) {
                    statusLabel.setText("Request sent to parent");
                    statusLabel.getStyleClass().setAll("status-success");
                } else {
                    statusLabel.setText("Request failed");
                    statusLabel.getStyleClass().setAll("status-error");
                }

            } catch (Exception ex) {
                statusLabel.setText("Invalid request");
                statusLabel.getStyleClass().setAll("status-error");
            }
        });

        logout.setOnAction(e -> {
            stage.close();
            new LoginFX().start(new Stage());
        });

        Scene scene = new Scene(grid, 520, 480);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void updateBalance(Label label) {
        double bal = bank.getChildBalance(child.getUsername());
        label.setText(String.format("%.2f", bal));
    }
}
