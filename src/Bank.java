import java.sql.*;

public class Bank {

    public Bank() {
        createTables();
    }

    private void createTables() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();

            st.execute("""
                CREATE TABLE IF NOT EXISTS Users(
                    username TEXT PRIMARY KEY,
                    password TEXT,
                    name TEXT,
                    type TEXT
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ChildAccount(
                    username TEXT PRIMARY KEY,
                    balance REAL,
                    spendingLimit REAL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ParentChild(
                    parentUsername TEXT,
                    childUsername TEXT
                )
            """);

            // ✅ MONEY REQUEST TABLE
            st.execute("""
                CREATE TABLE IF NOT EXISTS MoneyRequest(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    childUsername TEXT,
                    amount REAL,
                    status TEXT
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- AUTH ----------------

    public User login(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM Users WHERE username=? AND password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");

                if (type.equals("Parent"))
                    return new ParentAcc(username, password, name);
                else
                    return new ChildAcc(username, password, name, 0);
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public boolean usernameExists(String username) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username FROM Users WHERE username = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return true;
        }
    }

    public boolean registerParent(String username, String password, String name) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Users VALUES (?, ?, ?, 'Parent')"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean registerChild(String username, String password, String name, double limit) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps1 = conn.prepareStatement(
                    "INSERT INTO Users VALUES (?, ?, ?, 'Child')"
            );
            ps1.setString(1, username);
            ps1.setString(2, password);
            ps1.setString(3, name);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO ChildAccount VALUES (?, 0, ?)"
            );
            ps2.setString(1, username);
            ps2.setDouble(2, limit);
            ps2.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- PARENT FEATURES ----------------

    public boolean linkChild(String parent, String child) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement check = conn.prepareStatement(
                    "SELECT username FROM ChildAccount WHERE username = ?"
            );
            check.setString(1, child);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) return false;

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ParentChild VALUES (?, ?)"
            );
            ps.setString(1, parent);
            ps.setString(2, child);
            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean parentDeposit(String child, double amount) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ChildAccount SET balance = balance + ? WHERE username = ?"
            );
            ps.setDouble(1, amount);
            ps.setString(2, child);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public double getChildBalance(String username) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT balance FROM ChildAccount WHERE username = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("balance");

        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    // ---------------- CHILD SPEND ----------------

    public boolean childSpend(String username, double amount) {
        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ChildAccount SET balance = balance - ? WHERE username = ? AND balance >= ?"
            );
            ps.setDouble(1, amount);
            ps.setString(2, username);
            ps.setDouble(3, amount);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- MONEY REQUEST SYSTEM ----------------

    public boolean requestMoney(String childUsername, double amount) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO MoneyRequest (childUsername, amount, status) VALUES (?, ?, 'PENDING')"
            );
            ps.setString(1, childUsername);
            ps.setDouble(2, amount);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ResultSet getPendingRequests() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM MoneyRequest WHERE status = 'PENDING'"
            );
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean approveRequest(int id, String childUser, double amount) {
        try {
            Connection conn = DBConnection.getConnection();

            parentDeposit(childUser, amount);

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE MoneyRequest SET status = 'APPROVED' WHERE id = ?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean rejectRequest(int id) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE MoneyRequest SET status = 'REJECTED' WHERE id = ?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
