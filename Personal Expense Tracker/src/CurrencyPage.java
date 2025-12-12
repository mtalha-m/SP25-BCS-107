import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Arrays;
import java.util.List;

public class CurrencyPage extends BorderPane {
    private AppController controller;
    private ComboBox<String> currencyCombo;
    private Label currentCurrencyLabel;
    private Label symbolLabel;
    private Runnable onCurrencyChange;

    private static final List<String> CURRENCIES = Arrays.asList(
            "USD", "PKR", "SAR", "EUR", "INR", "GBP"
    );

    public CurrencyPage(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(40));

        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        Label titleLabel = new Label("💱 Currency Settings");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");

        Label infoTitle = new Label("Current Currency");
        infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        currentCurrencyLabel = new Label();
        currentCurrencyLabel.setFont(Font.font("Arial", 18));

        symbolLabel = new Label();
        symbolLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        infoBox.getChildren().addAll(infoTitle, currentCurrencyLabel, symbolLabel);

        VBox selectionBox = new VBox(15);
        selectionBox.setAlignment(Pos.CENTER);

        Label selectLabel = new Label("Select Currency:");
        selectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll(CURRENCIES);
        currencyCombo.setPrefWidth(200);

        Button applyButton = new Button("Apply Currency");
        applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        applyButton.setOnAction(e -> applyCurrency());

        selectionBox.getChildren().addAll(selectLabel, currencyCombo, applyButton);

        Label noteLabel = new Label("Note: Changing currency will update all transaction displays");
        noteLabel.setFont(Font.font("Arial", 11));
        noteLabel.setStyle("-fx-text-fill: #666;");

        container.getChildren().addAll(titleLabel, infoBox, selectionBox, noteLabel);

        setCenter(container);

        updateDisplay();
    }

    public void setOnCurrencyChange(Runnable callback) {
        this.onCurrencyChange = callback;
    }

    private void applyCurrency() {
        String selectedCurrency = currencyCombo.getValue();

        if (selectedCurrency == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a currency!");
            return;
        }

        Settings settings = controller.getSettings();
        settings.setCurrency(selectedCurrency);
        settings.save();

        showAlert(Alert.AlertType.INFORMATION, "Success",
                "Currency changed to " + selectedCurrency + " successfully!");

        updateDisplay();

        if (onCurrencyChange != null) {
            onCurrencyChange.run();
        }
    }

    public void updateDisplay() {
        Settings settings = controller.getSettings();
        String currency = settings.getCurrency();
        String symbol = settings.getCurrencySymbol();

        currentCurrencyLabel.setText(currency);
        symbolLabel.setText(symbol);
        currencyCombo.setValue(currency);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}