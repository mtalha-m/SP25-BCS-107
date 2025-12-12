// ==================== ReportGenerator.java ====================
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {

    public void exportToCSV(List<Transaction> list, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Date,Title,Type,Category,Amount,Note");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Transaction t : list) {
                writer.printf("%s,%s,%s,%s,%.2f,%s%n",
                        t.getDate().format(formatter),
                        t.getTitle(),
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getNote() != null ? t.getNote() : "");
            }

            System.out.println("CSV exported successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
        }
    }

    public void exportToTXT(List<Transaction> list, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("===============================================");
            writer.println("           TRANSACTION REPORT");
            writer.println("===============================================");
            writer.println();

            double totalIncome = 0;
            double totalExpense = 0;

            for (Transaction t : list) {
                writer.println(t.toString());
                if (t.isIncome()) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();
                }
            }

            writer.println();
            writer.println("===============================================");
            writer.printf("Total Income:  $%.2f%n", totalIncome);
            writer.printf("Total Expense: $%.2f%n", totalExpense);
            writer.printf("Net Balance:   $%.2f%n", totalIncome - totalExpense);
            writer.println("===============================================");

            System.out.println("TXT report exported successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting to TXT: " + e.getMessage());
        }
    }
}