// ==================== Settings.java ====================
import java.io.*;

public class Settings implements Serializable {
    private static final String SETTINGS_FILE = "settings.dat";
    private double budget;
    private double spent;
    private String currency;
    private String currencySymbol;

    public Settings() {
        this.budget = 0.0;
        this.spent = 0.0;
        this.currency = "USD";
        this.currencySymbol = "$";
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

    public void addSpent(double amount) {
        this.spent += amount;
    }

    public void subtractSpent(double amount) {
        this.spent -= amount;
    }

    public double getRemaining() {
        return budget - spent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        updateCurrencySymbol();
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    private void updateCurrencySymbol() {
        switch (currency) {
            case "PKR": currencySymbol = "Rs "; break;
            case "SAR": currencySymbol = "﷼ "; break;
            case "EUR": currencySymbol = "€"; break;
            case "INR": currencySymbol = "₹"; break;
            case "USD": currencySymbol = "$"; break;
            case "GBP": currencySymbol = "£"; break;
            default: currencySymbol = "$";
        }
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    public static Settings load() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Settings settings = (Settings) ois.readObject();
                settings.updateCurrencySymbol();
                return settings;
            } catch (Exception e) {
                System.err.println("Error loading settings: " + e.getMessage());
            }
        }
        return new Settings();
    }
}
