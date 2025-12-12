// ==================== AppController.java ====================
import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AppController {
    private TransactionManager transactionManager;
    private ReportGenerator reportGenerator;
    private Settings settings;
    private static final String TRANSACTIONS_FILE = "transactions.dat";
    private static final String SETTINGS_FILE = "settings.dat";

    public AppController() {
        this.transactionManager = new TransactionManager();
        this.reportGenerator = new ReportGenerator();
        this.settings = Settings.load();
        loadTransactions();
    }

    public Settings getSettings() {
        return settings;
    }

    public void addTransaction(String id, LocalDate date, String title, String category, double amount, boolean isIncome, String note) {
        Transaction transaction = new Transaction(id, date, title, category, amount, isIncome, note);
        transactionManager.addTransaction(transaction);

        if (!isIncome) {
            settings.addSpent(amount);
            settings.save();
        }

        saveTransactions();
    }

    public void updateTransaction(String id, LocalDate date, String title, String category, double amount, boolean isIncome, String note) {
        Transaction oldTransaction = transactionManager.getTransactionById(id);

        if (oldTransaction != null && !oldTransaction.isIncome()) {
            settings.subtractSpent(oldTransaction.getAmount());
        }

        Transaction transaction = new Transaction(id, date, title, category, amount, isIncome, note);
        transactionManager.updateTransaction(transaction);

        if (!isIncome) {
            settings.addSpent(amount);
        }
        settings.save();

        saveTransactions();
    }

    public void deleteTransaction(String id) {
        Transaction transaction = transactionManager.getTransactionById(id);
        if (transaction != null && !transaction.isIncome()) {
            settings.subtractSpent(transaction.getAmount());
            settings.save();
        }
        transactionManager.deleteTransaction(id);

        saveTransactions();
    }

    // NEW METHOD: Wipe all data
    public void clearAllData() {
        File tFile = new File(TRANSACTIONS_FILE);
        if (tFile.exists()) tFile.delete();

        File sFile = new File(SETTINGS_FILE);
        if (sFile.exists()) sFile.delete();

        transactionManager = new TransactionManager();
        settings = new Settings();
    }

    public Transaction getTransactionById(String id) {
        return transactionManager.getTransactionById(id);
    }

    public List<Transaction> getDailyTransactions(LocalDate date) {
        return transactionManager.getByDate(date);
    }

    public List<Transaction> getWeeklyTransactions(LocalDate date) {
        return transactionManager.getByWeek(date);
    }

    public List<Transaction> getMonthlyTransactions(YearMonth month) {
        return transactionManager.getByMonth(month);
    }

    public void exportCSV(List<Transaction> list, String filePath) {
        reportGenerator.exportToCSV(list, filePath);
    }

    public void exportTXT(List<Transaction> list, String filePath) {
        reportGenerator.exportToTXT(list, filePath);
    }

    public List<Transaction> getAllTransactions() {
        return transactionManager.getAllTransactions();
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTIONS_FILE))) {
            oos.writeObject(transactionManager.getAllTransactions());
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Transaction> transactions = (List<Transaction>) ois.readObject();
                for (Transaction t : transactions) {
                    transactionManager.addTransaction(t);
                }
            } catch (Exception e) {
                System.err.println("Error loading transactions: " + e.getMessage());
            }
        }
    }
}