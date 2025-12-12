// ==================== UserManager.java ====================
import java.io.*;

public class UserManager {
    private static final String USER_FILE = "user_credentials.dat";

    private static class UserCredentials implements Serializable {
        String username;
        String password;

        UserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private UserCredentials cachedUser;

    public UserManager() {
        loadUser();
    }

    public boolean isRegistered() {
        return cachedUser != null;
    }

    public boolean register(String username, String password) {
        if (isRegistered()) {
            return false;
        }
        cachedUser = new UserCredentials(username, password);
        saveUser();
        return true;
    }

    public boolean login(String username, String password) {
        if (!isRegistered()) return false;
        return cachedUser.username.equals(username) && cachedUser.password.equals(password);
    }

    // NEW METHOD: Delete the user account
    public void deleteAccount() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            file.delete();
        }
        cachedUser = null;
    }

    private void saveUser() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(cachedUser);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    private void loadUser() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                cachedUser = (UserCredentials) ois.readObject();
            } catch (Exception e) {
                System.err.println("Error loading user: " + e.getMessage());
            }
        }
    }
}