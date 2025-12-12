// ==================== CalendarPage.java ====================
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendarPage extends BorderPane {
    private AppController controller;
    private TextArea displayArea;
    private DatePicker datePicker;

    public CalendarPage(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(20));

        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));

        Label dateLabel = new Label("Select Date:");
        dateLabel.setFont(Font.font("Arial", 14));
        topPanel.getChildren().add(dateLabel);

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(200);
        datePicker.setOnAction(e -> displayCalendar());
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
        topPanel.getChildren().add(datePicker);

        setTop(topPanel);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setFont(Font.font("Monospaced", 12));
        displayArea.setWrapText(true);
        setCenter(displayArea);

        displayCalendar();
    }

    public void displayCalendar() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        Settings settings = controller.getSettings();
        String symbol = settings.getCurrencySymbol();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        List<Transaction> transactions = controller.getMonthlyTransactions(yearMonth);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("╔════════════════════════════════════════════════╗\n"));
        sb.append(String.format("║  %s %d - Monthly Summary%s║\n",
                yearMonth.getMonth(),
                yearMonth.getYear(),
                " ".repeat(Math.max(0, 17 - yearMonth.getMonth().toString().length()))));
        sb.append(String.format("╚════════════════════════════════════════════════╝\n\n"));

        if (transactions.isEmpty()) {
            sb.append("📝 No transactions for this month.\n");
        } else {
            sb.append(String.format("%-12s %-20s %-15s %-12s\n",
                    "Date", "Title", "Category", "Amount"));
            sb.append("─".repeat(80)).append("\n");

            for (Transaction t : transactions) {
                String typeIcon = t.isIncome() ? "💰" : "💸";
                sb.append(String.format("%-12s %s %-18s %s %-12s %s%-11.2f\n",
                        t.getFormattedDate(),
                        typeIcon,
                        t.getTitle(),
                        t.getCategoryIcon(),
                        t.getCategory(),
                        symbol,
                        t.getAmount()));
            }

            double totalIncome = transactions.stream()
                    .filter(Transaction::isIncome)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpense = transactions.stream()
                    .filter(t -> !t.isIncome())
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            sb.append("\n");
            sb.append("═".repeat(80)).append("\n");
            sb.append(String.format("💰 Total Income:   %s%.2f\n", symbol, totalIncome));
            sb.append(String.format("💸 Total Expense:  %s%.2f\n", symbol, totalExpense));
            sb.append(String.format("📊 Net Balance:    %s%.2f\n", symbol, totalIncome - totalExpense));
            sb.append("═".repeat(80)).append("\n");
        }

        displayArea.setText(sb.toString());
    }
}
