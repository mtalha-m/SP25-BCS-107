// ==================== Transaction.java ====================
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private LocalDate date;
    private String title;
    private String category;
    private double amount;
    private boolean isIncome;
    private String note;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Transaction(String id, LocalDate date, String title, String category, double amount, boolean isIncome, String note) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.isIncome = isIncome;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return isIncome ? "Income" : "Expense";
    }

    public String getCategoryIcon() {
        switch (category.toLowerCase()) {
            case "food": return "🍔";
            case "entertainment": return "🎬";
            case "groceries": return "🛒";
            case "transportation": return "🚗";
            case "bills and fees": return "📄";
            case "extras": return "✨";
            case "shopping": return "🛍️";
            default: return "📝";
        }
    }

    public String getFormattedDate() {
        return date.format(FORMATTER);
    }

    // This method is critical for ReportsPage
    public String toString(String currencySymbol) {
        return String.format("%s | %s | %s %s | %s%.2f | %s",
                getFormattedDate(),
                title,
                getCategoryIcon(),
                category,
                currencySymbol,
                amount,
                note != null && !note.isEmpty() ? note : "No note");
    }

    @Override
    public String toString() {
        return toString("$");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Transaction that = (Transaction) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}