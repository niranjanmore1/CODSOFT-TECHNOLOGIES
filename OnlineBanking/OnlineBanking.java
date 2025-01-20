import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class OnlineBanking {
    private static HashMap<String, Account> accounts = new HashMap<>();
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccounts();
        while (true) {
            printMainBanner();
            System.out.println("1. Manager Login");
            System.out.println("2. Accountant Login");
            System.out.println("3. Customer Login");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> login("manager", "admin123", OnlineBanking::managerMenu);
                case 2 -> login("accountant", "accountant123", OnlineBanking::accountantMenu);
                case 3 -> customerLogin();
                case 4 -> {
                    saveAccounts();
                    System.out.println("Thank you for using State Bank of India Management System.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void login(String role, String password, Runnable menu) {
        System.out.print("Enter " + role + " Password: ");
        String inputPassword = scanner.nextLine();
        if (!password.equals(inputPassword)) {
            System.out.println("Invalid password.");
        } else {
            menu.run();
        }
    }

    private static void printMainBanner() {
        System.out.println("\n=========================================");
        System.out.println("     State Bank of India, Pune Branch");
        System.out.println("=========================================");
        System.out.println("    Contact Us: sbi.pune@statebank.com");
        System.out.println("          Phone: +91-20-12345678");
        System.out.println("=========================================");
    }

    private static void managerMenu() {
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. View All Accounts");
            System.out.println("2. Activate/Deactivate an Account");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewAllAccounts();
                case 2 -> toggleAccountStatus();
                case 3 -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void accountantMenu() {
        while (true) {
            System.out.println("\n--- Accountant Menu ---");
            System.out.println("1. View All Transactions for All Accounts");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> accounts.values().forEach(Account::printTransactionHistory);
                case 2 -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void customerLogin() {
        System.out.print("Enter Account ID: ");
        String accountId = scanner.nextLine();
        Account account = accounts.get(accountId);
        if (account == null || !account.isActive()) {
            System.out.println("Account not found or inactive.");
            return;
        }
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        if (!account.getPin().equals(pin)) {
            System.out.println("Invalid PIN.");
            return;
        }
        customerMenu(account);
    }

    private static void customerMenu(Account account) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. View Transaction History");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> System.out.println("Current Balance: $" + account.getBalance());
                case 2 -> account.printTransactionHistory();
                case 3 -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void toggleAccountStatus() {
        System.out.print("Enter Account ID: ");
        String accountId = scanner.nextLine();
        Account account = accounts.get(accountId);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        if (account.isActive()) {
            account.deactivate();
            System.out.println("Account deactivated.");
        } else {
            account.activate();
            System.out.println("Account activated.");
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n--- All Accounts ---");
        accounts.forEach((id, account) -> {
            System.out.println("ID: " + id + ", Name: " + account.getName() + ", Active: " + account.isActive());
        });
    }

    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof HashMap) {
                accounts = (HashMap<String, Account>) obj;
            } else {
                System.out.println("Invalid data format in the accounts file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data.");
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }
}



/*Final Passwords:
Manager: admin123
Accountant: accountant123
Staff: staff123*/