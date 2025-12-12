// ==================== Main.java ====================
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private boolean isDarkMode = false;
    private BorderPane mainLayout;
    private Scene scene;
    private String currentUser;
    private Label usernameLabel;
    private ContextMenu hamburgerMenu;
    private AppController appController; // Keep a reference

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Management System");

        UserManager userManager = new UserManager();
        LoginPage loginPage = new LoginPage(userManager);

        Scene loginScene = new Scene(loginPage, 900, 600);
        primaryStage.setScene(loginScene);

        loginPage.setOnLoginSuccess(() -> {
            currentUser = loginPage.getUsername();
            showMainApplication(primaryStage);
        });

        primaryStage.show();
    }

    private void showMainApplication(Stage primaryStage) {
        appController = new AppController();

        mainLayout = new BorderPane();

        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);

        StackPane contentPane = new StackPane();

        CalendarPage calendarPage = new CalendarPage(appController);
        ManageTransactionPage managePage = new ManageTransactionPage(appController);
        ReportsPage reportsPage = new ReportsPage(appController);
        BudgetPage budgetPage = new BudgetPage(appController);
        CurrencyPage currencyPage = new CurrencyPage(appController);

        contentPane.getChildren().addAll(calendarPage, managePage, reportsPage, budgetPage, currencyPage);

        showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 0);

        managePage.setOnTransactionChange(() -> {
            calendarPage.displayCalendar();
            budgetPage.updateDisplay();
        });

        budgetPage.setOnBudgetUpdate(() -> {
            // Budget updated
        });

        currencyPage.setOnCurrencyChange(() -> {
            calendarPage.displayCalendar();
            managePage.refreshTransactionList();
            budgetPage.updateDisplay();
        });

        hamburgerMenu = createHamburgerMenu(
                calendarPage, managePage, reportsPage, budgetPage, currencyPage
        );

        mainLayout.setCenter(contentPane);

        scene = new Scene(mainLayout, 1000, 700);
        applyTheme();
        primaryStage.setScene(scene);

        calendarPage.displayCalendar();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #2196F3;");

        Button menuButton = new Button("☰");
        menuButton.setStyle(
                "-fx-font-size: 20px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-cursor: hand;"
        );
        menuButton.setOnAction(e -> {
            hamburgerMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 5);
        });

        Label appTitle = new Label("Expense Management System");
        appTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        appTitle.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        usernameLabel = new Label("👤 " + currentUser);
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        usernameLabel.setStyle("-fx-text-fill: white;");

        topBar.getChildren().addAll(menuButton, appTitle, spacer, usernameLabel);

        return topBar;
    }

    private ContextMenu createHamburgerMenu(
            CalendarPage calendarPage,
            ManageTransactionPage managePage,
            ReportsPage reportsPage,
            BudgetPage budgetPage,
            CurrencyPage currencyPage
    ) {
        ContextMenu menu = new ContextMenu();

        MenuItem calendarItem = new MenuItem("📅 View Calendar");
        calendarItem.setOnAction(e -> {
            showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 0);
            calendarPage.displayCalendar();
        });

        MenuItem manageItem = new MenuItem("💼 Manage Transactions");
        manageItem.setOnAction(e -> {
            showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 1);
        });

        MenuItem reportsItem = new MenuItem("📊 Reports");
        reportsItem.setOnAction(e -> {
            showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 2);
        });

        MenuItem budgetItem = new MenuItem("💰 Budget");
        budgetItem.setOnAction(e -> {
            showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 3);
        });

        MenuItem currencyItem = new MenuItem("💱 Currency");
        currencyItem.setOnAction(e -> {
            showPage(calendarPage, managePage, reportsPage, budgetPage, currencyPage, 4);
            currencyPage.updateDisplay();
        });

        SeparatorMenuItem separator1 = new SeparatorMenuItem();

        Menu themeMenu = new Menu("🎨 Theme");
        RadioMenuItem lightTheme = new RadioMenuItem("☀️ Light Mode");
        RadioMenuItem darkTheme = new RadioMenuItem("🌙 Dark Mode");

        ToggleGroup themeGroup = new ToggleGroup();
        lightTheme.setToggleGroup(themeGroup);
        darkTheme.setToggleGroup(themeGroup);
        lightTheme.setSelected(true);

        lightTheme.setOnAction(e -> {
            isDarkMode = false;
            applyTheme();
        });

        darkTheme.setOnAction(e -> {
            isDarkMode = true;
            applyTheme();
        });

        themeMenu.getItems().addAll(lightTheme, darkTheme);

        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        MenuItem aboutItem = new MenuItem("ℹ️ About");
        aboutItem.setOnAction(e -> showAbout());

        MenuItem deleteAccountItem = new MenuItem("❌ Delete Account");
        deleteAccountItem.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Account");
            confirm.setHeaderText("Delete your account and all data?");
            confirm.setContentText("This action cannot be undone. All transactions and settings will be lost.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // 1. Delete User
                    new UserManager().deleteAccount();

                    // 2. Delete All Data (Transactions & Settings)
                    if (appController != null) {
                        appController.clearAllData();
                    }

                    // 3. Restart Application to Login Screen
                    Stage stage = (Stage) scene.getWindow();
                    start(stage);
                }
            });
        });

        MenuItem logoutItem = new MenuItem("🚪 Logout");
        logoutItem.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Logout");
            confirm.setHeaderText("Are you sure you want to logout?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Stage stage = (Stage) scene.getWindow();
                    start(stage);
                }
            });
        });

        menu.getItems().addAll(
                calendarItem,
                manageItem,
                reportsItem,
                budgetItem,
                currencyItem,
                separator1,
                themeMenu,
                separator2,
                aboutItem,
                deleteAccountItem, // Added here
                logoutItem
        );

        return menu;
    }

    private void showPage(
            CalendarPage calendarPage,
            ManageTransactionPage managePage,
            ReportsPage reportsPage,
            BudgetPage budgetPage,
            CurrencyPage currencyPage,
            int index
    ) {
        calendarPage.setVisible(index == 0);
        managePage.setVisible(index == 1);
        reportsPage.setVisible(index == 2);
        budgetPage.setVisible(index == 3);
        currencyPage.setVisible(index == 4);
    }

    private void applyTheme() {
        if (scene == null) return;

        if (isDarkMode) {
            scene.getRoot().setStyle(
                    "-fx-base: #2b2b2b; " +
                            "-fx-background: #1e1e1e; " +
                            "-fx-control-inner-background: #3c3c3c; " +
                            "-fx-text-base-color: #e0e0e0;"
            );
        } else {
            scene.getRoot().setStyle("");
        }
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Expense Management System");
        alert.setContentText(
                "Version 3.2\n\n" +
                        "Features:\n" +
                        "• Transaction Management\n" +
                        "• Budget Tracking\n" +
                        "• Multi-Currency Support\n" +
                        "• Reports & Export\n" +
                        "• Account Management"
        );
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}