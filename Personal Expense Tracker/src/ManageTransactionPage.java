// ==================== ManageTransactionPage.java ====================
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class ManageTransactionPage extends BorderPane {
    private AppController controller;
    private DatePicker datePicker;
    private TextField titleField;
    private ComboBox<String> categoryCombo;
    private TextField amountField;
    private CheckBox isIncomeCheckBox;
    private TextArea noteArea;
    private ListView<Transaction> transactionListView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Transaction selectedTransaction;
    private Runnable onTransactionChange;

    private static final List<String> CATEGORIES = Arrays.asList(
            "Food", "Entertainment", "Groceries", "Transportation",
            "Bills and Fees", "Extras", "Shopping"
    );

    public ManageTransactionPage(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(20));

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4);

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        splitPane.getItems().addAll(leftPanel, rightPanel);
        setCenter(splitPane);

        refreshTransactionList();
    }

    public void setOnTransactionChange(Runnable callback) {
        this.onTransactionChange = callback;
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));

        Label titleLabel = new Label("Transaction List");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        transactionListView = new ListView<>();
        transactionListView.setCellFactory(lv -> new ListCell<Transaction>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Settings settings = controller.getSettings();
                    String symbol = settings.getCurrencySymbol();
                    setText(String.format("%s %s - %s %s - %s%.2f",
                            item.isIncome() ? "💰" : "💸",
                            item.getTitle(),
                            item.getCategoryIcon(),
                            item.getCategory(),
                            symbol,
                            item.getAmount()));
                }
            }
        });

        transactionListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        loadTransactionForEdit(newVal);
                    }
                }
        );

        Button refreshButton = new Button("Refresh List");
        refreshButton.setOnAction(e -> refreshTransactionList());

        panel.getChildren().addAll(titleLabel, transactionListView, refreshButton);
        VBox.setVgrow(transactionListView, Priority.ALWAYS);

        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));

        Label titleLabel = new Label("Manage Transaction");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        int row = 0;

        formGrid.add(new Label("Date:"), 0, row);
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(250);
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            private java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ?
                        LocalDate.parse(string, formatter) : null;
            }
        });
        formGrid.add(datePicker, 1, row++);

        formGrid.add(new Label("Title:"), 0, row);
        titleField = new TextField();
        titleField.setPromptText("Enter transaction title");
        titleField.setPrefWidth(250);
        formGrid.add(titleField, 1, row++);

        formGrid.add(new Label("Category:"), 0, row);
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(CATEGORIES);
        categoryCombo.setValue("Food");
        categoryCombo.setPrefWidth(250);
        formGrid.add(categoryCombo, 1, row++);

        formGrid.add(new Label("Amount:"), 0, row);
        amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.setPrefWidth(250);
        formGrid.add(amountField, 1, row++);

        formGrid.add(new Label("Is Income:"), 0, row);
        isIncomeCheckBox = new CheckBox();
        formGrid.add(isIncomeCheckBox, 1, row++);

        formGrid.add(new Label("Note:"), 0, row);
        noteArea = new TextArea();
        noteArea.setPromptText("Optional note");
        noteArea.setPrefRowCount(3);
        noteArea.setPrefWidth(250);
        formGrid.add(noteArea, 1, row++);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        addButton = new Button("Add Transaction");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> handleAdd());

        updateButton = new Button("Update Transaction");
        updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(e -> handleUpdate());
        updateButton.setDisable(true);

        deleteButton = new Button("Delete Transaction");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> handleDelete());
        deleteButton.setDisable(true);

        Button clearButton = new Button("Clear Form");
        clearButton.setOnAction(e -> clearForm());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        panel.getChildren().addAll(titleLabel, formGrid, buttonBox);

        return panel;
    }

    private void handleAdd() {
        try {
            LocalDate date = datePicker.getValue();
            String title = titleField.getText().trim();
            String category = categoryCombo.getValue();
            double amount = Double.parseDouble(amountField.getText().trim());
            boolean isIncome = isIncomeCheckBox.isSelected();
            String note = noteArea.getText().trim();

            if (title.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a title!");
                return;
            }

            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be positive!");
                return;
            }

            controller.addTransaction(null, date, title, category, amount, isIncome, note);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully!");

            clearForm();
            refreshTransactionList();

            if (onTransactionChange != null) {
                onTransactionChange.run();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount! Enter a valid number");
        }
    }

    private void handleUpdate() {
        if (selectedTransaction == null) return;

        try {
            LocalDate date = datePicker.getValue();
            String title = titleField.getText().trim();
            String category = categoryCombo.getValue();
            double amount = Double.parseDouble(amountField.getText().trim());
            boolean isIncome = isIncomeCheckBox.isSelected();
            String note = noteArea.getText().trim();

            if (title.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a title!");
                return;
            }

            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be positive!");
                return;
            }

            controller.updateTransaction(selectedTransaction.getId(), date, title, category, amount, isIncome, note);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction updated successfully!");

            clearForm();
            refreshTransactionList();

            if (onTransactionChange != null) {
                onTransactionChange.run();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount! Enter a valid number");
        }
    }

    private void handleDelete() {
        if (selectedTransaction == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Transaction");
        confirm.setContentText("Are you sure you want to delete this transaction?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controller.deleteTransaction(selectedTransaction.getId());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction deleted successfully!");
                clearForm();
                refreshTransactionList();

                if (onTransactionChange != null) {
                    onTransactionChange.run();
                }
            }
        });
    }

    private void loadTransactionForEdit(Transaction transaction) {
        selectedTransaction = transaction;

        datePicker.setValue(transaction.getDate());
        titleField.setText(transaction.getTitle());
        categoryCombo.setValue(transaction.getCategory());
        amountField.setText(String.valueOf(transaction.getAmount()));
        isIncomeCheckBox.setSelected(transaction.isIncome());
        noteArea.setText(transaction.getNote());

        addButton.setDisable(true);
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void clearForm() {
        selectedTransaction = null;
        datePicker.setValue(LocalDate.now());
        titleField.clear();
        categoryCombo.setValue("Food");
        amountField.clear();
        isIncomeCheckBox.setSelected(false);
        noteArea.clear();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        transactionListView.getSelectionModel().clearSelection();
    }

    public void refreshTransactionList() {
        transactionListView.getItems().clear();
        transactionListView.getItems().addAll(controller.getAllTransactions());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
