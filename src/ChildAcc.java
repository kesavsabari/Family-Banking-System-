public class ChildAcc extends User {
    private double balance;
    private double spendingLimit;

    public ChildAcc(String username, String password, String name, double spendingLimit) {
        super(username, password, name);
        this.spendingLimit = spendingLimit;
        this.balance = 0;
    }

    public double getBalance() {
        return balance;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public void addMoney(double amount) {
        balance += amount;
    }

    public boolean spend(double amount) {
        if (amount > spendingLimit || amount > balance) return false;
        balance -= amount;
        return true;
    }
}
