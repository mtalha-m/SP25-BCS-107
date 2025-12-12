// ==================== ReportsPage.java ====================
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ReportsPage extends BorderPane {
    private AppController controller;
    private TextArea reportArea;

    public ReportsPage(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(20));

        Label titleLabel = new Label("Reports & Export");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 20, 0));
        setTop(titleBox);

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);

        Button dailyButton = new Button("Daily Report");
        dailyButton.setOnAction(e -> requestDailyReport(LocalDate.now()));
        buttonPanel.getChildren().add(dailyButton);

        Button weeklyButton = new Button("Weekly Report");
        weeklyButton.setOnAction(e -> requestWeeklyReport(LocalDate.now()));
        buttonPanel.getChildren().add(weeklyButton);

        Button monthlyButton = new Button("Monthly Report");
        monthlyButton.setOnAction(e -> requestMonthlyReport(YearMonth.now()));
        buttonPanel.getChildren().add(monthlyButton);

        Button exportCSVButton = new Button("Export CSV");
        exportCSVButton.setOnAction(e -> exportCSV());
        buttonPanel.getChildren().add(exportCSVButton);

        Button exportTXTButton = new Button("Export TXT");
        exportTXTButton.setOnAction(e -> exportTXT());
        buttonPanel.getChildren().add(exportTXTButton);

        setBottom(buttonPanel);

        reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setFont(Font.font("Monospaced", 12));
        setCenter(reportArea);
    }

    public void requestDailyReport(LocalDate date) {
        List<Transaction> transactions = controller.getDailyTransactions(date);
        displayReport("Daily Report - " + date, transactions);
    }

    public void requestWeeklyReport(LocalDate date) {
        List<Transaction> transactions = controller.getWeeklyTransactions(date);
        displayReport("Weekly Report (Week of " + date + ")", transactions);
    }

    public void requestMonthlyReport(YearMonth month) {
        List<Transaction> transactions = controller.getMonthlyTransactions(month);
        displayReport("Monthly Report - " + month, transactions);
    }

    private void displayReport(String title, List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append(title).append("\n");
        sb.append("========================================\n\n");

        if (transactions.isEmpty()) {
            sb.append("No transactions found.\n");
        } else {
            for (Transaction t : transactions) {
                sb.append(t.toString()).append("\n");
            }

            double totalIncome = transactions.stream()
                    .filter(Transaction::isIncome)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpense = transactions.stream()
                    .filter(t -> !t.isIncome())
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            sb.append("\n--- Summary ---\n");
            sb.append(String.format("Total Income: $%.2f\n", totalIncome));
            sb.append(String.format("Total Expense: $%.2f\n", totalExpense));
            sb.append(String.format("Net Balance: $%.2f\n", totalIncome - totalExpense));
        }

        reportArea.setText(sb.toString());
    }

    private void exportCSV() {
        List<Transaction> transactions = controller.getAllTransactions();
        if (transactions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No transactions to export!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.setInitialFileName("transactions.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            controller.exportCSV(transactions, file.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Success", "CSV exported successfully!");
        }
    }

    private void exportTXT() {
        List<Transaction> transactions = controller.getAllTransactions();
        if (transactions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No transactions to export!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save TXT File");
        fileChooser.setInitialFileName("transactions.txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            controller.exportTXT(transactions, file.getAbsolutePath());
            showAlert(Alert.AlertType.INFORMATION, "Success", "TXT exported successfully!");
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