import java.util.Scanner;

public class Main {

    static Bank bank = new Bank();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("      WELCOME TO FAMILY BANK SYSTEM   ");
        System.out.println("======================================");

        while (true) {
            System.out.println("\n--------- MAIN MENU ---------");
            System.out.println("1. Register Parent");
            System.out.println("2. Register Child");
            System.out.println("3. Login");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> registerParent();
                case 2 -> registerChild();
                case 3 -> login();
                case 4 -> {
                    System.out.println("\nThank you for using the system.");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // ---------------- REGISTER PARENT ----------------
    static void registerParent() {
        System.out.println("\n--- Parent Registration ---");

        System.out.print("Username: ");
        String u = sc.nextLine();

        System.out.print("Password: ");
        String p = sc.nextLine();

        System.out.print("Name: ");
        String n = sc.nextLine();

        boolean ok = bank.registerParent(u, p, n);

        if (ok)
            System.out.println("Parent registered successfully.");
        else
            System.out.println("Username already exists.");
    }

    // ---------------- REGISTER CHILD ----------------
    static void registerChild() {
        System.out.println("\n--- Child Registration ---");

        System.out.print("Username: ");
        String u = sc.nextLine();

        System.out.print("Password: ");
        String p = sc.nextLine();

        System.out.print("Name: ");
        String n = sc.nextLine();

        System.out.print("Spending Limit: ");
        double limit = sc.nextDouble();
        sc.nextLine();

        boolean ok = bank.registerChild(u, p, n, limit);

        if (ok)
            System.out.println("Child account created successfully.");
        else
            System.out.println("Username already exists.");
    }

    // ---------------- LOGIN ----------------
    static void login() {
        System.out.println("\n--- Login ---");

        System.out.print("Username: ");
        String u = sc.nextLine();

        System.out.print("Password: ");
        String p = sc.nextLine();

        User user = bank.login(u, p);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (user instanceof ParentAcc parent) {
            parentMenu(parent);
        } else {
            childMenu((ChildAcc) user);
        }
    }

    // ---------------- PARENT MENU ----------------
    static void parentMenu(ParentAcc parent) {
        while (true) {
            System.out.println("\n--------- PARENT DASHBOARD ---------");
            System.out.println("1. Link Child");
            System.out.println("2. Deposit to Child");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> {
                    System.out.print("Enter Child Username: ");
                    String cu = sc.nextLine();

                    boolean ok = bank.linkChild(parent.getUsername(), cu);
                    if (ok)
                        System.out.println("Child linked successfully.");
                    else
                        System.out.println("Linking failed. Please Register your Child.");
                }

                case 2 -> {
                    System.out.print("Enter Child Username: ");
                    String cu = sc.nextLine();

                    System.out.print("Amount to deposit: ");
                    double amt = sc.nextDouble();
                    sc.nextLine();

                    boolean ok = bank.parentDeposit(cu, amt);

                    if (ok)
                        System.out.println("Money deposited successfully.");
                    else
                        System.out.println("Deposit failed. Child does not exist.");

                }

                case 3 -> {
                    System.out.println("Logged out from parent dashboard.");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------------- CHILD MENU ----------------
    static void childMenu(ChildAcc child) {
        while (true) {
            System.out.println("\n--------- CHILD DASHBOARD ---------");
            System.out.println("1. View Balance");
            System.out.println("2. Spend Money");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> System.out.println("Balance: " + child.getBalance());

                case 2 -> {
                    System.out.print("Amount to spend: ");
                    double amt = sc.nextDouble();
                    sc.nextLine();

                    boolean ok = bank.childSpend(child.getUsername(), amt);
                    if (ok)
                        System.out.println("Transaction successful.");
                    else
                        System.out.println("Spending limit exceeded or insufficient balance.");
                }

                case 3 -> {
                    System.out.println("Logged out from child dashboard.");
                    return;
                }

                default -> System.out.println("Invalid option.");
            }
        }
    }
}
