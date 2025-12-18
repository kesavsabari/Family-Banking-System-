import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginFX extends Application {

    private final Bank bank = new Bank();

    @Override
    public void start(Stage stage) {

        Label title = new Label("Family Banking System");
        title.getStyleClass().add("title-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.setPrefWidth(220);
        registerButton.setPrefWidth(220);

        Label statusLabel = new Label("");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User user = bank.login(username, password);

            if (user == null) {
                statusLabel.setText("Invalid username or password.");
                statusLabel.getStyleClass().setAll("status-error");
            } else {
                stage.close();

                if (user instanceof ParentAcc) {
                    new ParentDashboardFX((ParentAcc) user).show();
                } else {
                    new ChildDashboardFX((ChildAcc) user).show();
                }
            }
        });

        registerButton.setOnAction(e -> new RegisterFX().show());

        VBox root = new VBox(15, title, usernameField, passwordField,
                loginButton, registerButton, statusLabel);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 420, 380);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
