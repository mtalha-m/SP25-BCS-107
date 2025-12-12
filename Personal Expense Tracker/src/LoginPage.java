// ==================== LoginPage.java ====================
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginPage extends BorderPane {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button actionButton;
    private UserManager userManager;
    private Runnable onLoginSuccess;
    private String loggedInUsername;

    public LoginPage(UserManager userManager) {
        this.userManager = userManager;
        setPadding(new Insets(50));

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);

        Label titleLabel = new Label("💰 Expense Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Determine mode: First Time Setup vs Login
        boolean isFirstTime = !userManager.isRegistered();
        String subtitleText = isFirstTime ? "First Time Setup: Create Account" : "Welcome Back! Please Login";

        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Arial", 14));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);

        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        actionButton = new Button(isFirstTime ? "Create Account" : "Login");
        actionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");

        // Set action based on mode
        if (isFirstTime) {
            actionButton.setOnAction(e -> handleRegister());
        } else {
            actionButton.setOnAction(e -> handleLogin());
        }

        buttonBox.getChildren().add(actionButton);
        passwordField.setOnAction(e -> actionButton.fire());

        container.getChildren().addAll(titleLabel, subtitleLabel, formGrid, buttonBox);

        setCenter(container);
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    public String getUsername() {
        return loggedInUsername;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (userManager.login(username, password)) {
            loggedInUsername = username;
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
            passwordField.clear();
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both username and password!");
            return;
        }

        if (password.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 4 characters long!");
            return;
        }

        if (userManager.register(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created! Logging you in...");
            loggedInUsername = username;
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}