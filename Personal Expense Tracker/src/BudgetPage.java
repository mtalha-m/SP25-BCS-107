import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BudgetPage extends BorderPane {
    private AppController controller;
    private TextField budgetField;
    private Label spentLabel;
    private Label remainingLabel;
    private ProgressBar progressBar;
    private Runnable onBudgetUpdate;

    public BudgetPage(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(40));

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        Label titleLabel = new Label("💰 Budget Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        Label budgetLabel = new Label("Set Monthly Budget:");
        budgetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        budgetField = new TextField();
        budgetField.setPromptText("Enter budget amount");
        budgetField.setPrefWidth(250);

        Button setBudgetButton = new Button("Set Budget");
        setBudgetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        setBudgetButton.setOnAction(e -> setBudget());

        formGrid.add(budgetLabel, 0, 0);
        formGrid.add(budgetField, 0, 1);
        formGrid.add(setBudgetButton, 0, 2);

        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        Label statsTitle = new Label("Budget Overview");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        spentLabel = new Label();
        spentLabel.setFont(Font.font("Arial", 14));

        remainingLabel = new Label();
        remainingLabel.setFont(Font.font("Arial", 14));

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setPrefHeight(25);

        statsBox.getChildren().addAll(statsTitle, spentLabel, remainingLabel, progressBar);

        Button resetButton = new Button("Reset Budget");
        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetBudget());

        container.getChildren().addAll(titleLabel, formGrid, statsBox, resetButton);

        setCenter(container);

        updateDisplay();
    }

    public void setOnBudgetUpdate(Runnable callback) {
        this.onBudgetUpdate = callback;
    }

    private void setBudget() {
        try {
            double budget = Double.parseDouble(budgetField.getText().trim());

            if (budget <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Budget must be positive!");
                return;
            }

            Settings settings = controller.getSettings();
            settings.setBudget(budget);
            settings.save();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Budget set successfully!");
            updateDisplay();

            if (onBudgetUpdate != null) {
                onBudgetUpdate.run();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number!");
        }
    }

    private void resetBudget() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Budget");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This will reset your budget and spent amount.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Settings settings = controller.getSettings();
                settings.setBudget(0);
                settings.setSpent(0);
                settings.save();
                budgetField.clear();
                updateDisplay();

                if (onBudgetUpdate != null) {
                    onBudgetUpdate.run();
                }
            }
        });
    }

    public void updateDisplay() {
        Settings settings = controller.getSettings();
        String symbol = settings.getCurrencySymbol();

        double budget = settings.getBudget();
        double spent = settings.getSpent();
        double remaining = settings.getRemaining();

        if (budget > 0) {
            budgetField.setText(String.valueOf(budget));
            spentLabel.setText(String.format("Spent: %s%.2f", symbol, spent));
            remainingLabel.setText(String.format("Remaining: %s%.2f", symbol, remaining));

            double progress = Math.min(spent / budget, 1.0);
            progressBar.setProgress(progress);

            if (progress >= 1.0) {
                progressBar.setStyle("-fx-accent: #f44336;");
                remainingLabel.setStyle("-fx-text-fill: #f44336;");
            } else if (progress >= 0.8) {
                progressBar.setStyle("-fx-accent: #ff9800;");
                remainingLabel.setStyle("-fx-text-fill: #ff9800;");
            } else {
                progressBar.setStyle("-fx-accent: #4CAF50;");
                remainingLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        } else {
            spentLabel.setText("No budget set");
            remainingLabel.setText("Set a budget to track spending");
            progressBar.setProgress(0);
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