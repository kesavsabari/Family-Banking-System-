public class ParentAcc extends User {

    public ParentAcc(String username, String password, String name) {
        super(username, password, name);
    }

    public void depositToChild(ChildAcc child, double amount) {
        child.addMoney(amount);
    }
}
