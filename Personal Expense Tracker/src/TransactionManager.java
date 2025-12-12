// ==================== TransactionManager.java ====================
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionManager {
    private List<Transaction> allTransactions;

    public TransactionManager() {
        this.allTransactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        if (t.getId() == null || t.getId().isEmpty()) {
            t.setId(UUID.randomUUID().toString());
        }
        allTransactions.add(t);
    }

    public void deleteTransaction(String id) {
        allTransactions.removeIf(t -> t.getId().equals(id));
    }

    public void updateTransaction(Transaction updated) {
        for (int i = 0; i < allTransactions.size(); i++) {
            if (allTransactions.get(i).getId().equals(updated.getId())) {
                allTransactions.set(i, updated);
                break;
            }
        }
    }

    public Transaction getTransactionById(String id) {
        return allTransactions.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Transaction> getByDate(LocalDate date) {
        return allTransactions.stream()
                .filter(t -> t.getDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Transaction> getByWeek(LocalDate date) {
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        return allTransactions.stream()
                .filter(t -> !t.getDate().isBefore(startOfWeek) && !t.getDate().isAfter(endOfWeek))
                .collect(Collectors.toList());
    }

    public List<Transaction> getByMonth(YearMonth month) {
        return allTransactions.stream()
                .filter(t -> {
                    YearMonth transactionMonth = YearMonth.from(t.getDate());
                    return transactionMonth.equals(month);
                })
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(allTransactions);
    }
}