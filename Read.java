import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Read {

    public void readUser(int id, String name) {
        // CHANGED: Use try-with-resources for Connection
        try (Connection con = Dbconnection.getConnection()) {

            // CHANGED: Use try-with-resources for PreparedStatement and ResultSet
            String checkQuery = "SELECT * FROM bakery_users WHERE user_id = ?";
            try (PreparedStatement checkId = con.prepareStatement(checkQuery)) {
                checkId.setInt(1, id);
                try (ResultSet idResult = checkId.executeQuery()) {
                    if (!idResult.next()) {
                        System.out.println("UserId does not exist for displaying data :" + id);
                        return;
                    }
                }
            }

            // CHANGED: Use try-with-resources for user data query
            String selectQuery = "SELECT * FROM bakery_users WHERE user_id = ? AND full_name = ?";
            try (PreparedStatement ps = con.prepareStatement(selectQuery)) {
                ps.setInt(1, id);
                ps.setString(2, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("User ID: " + rs.getInt("user_id"));
                        System.out.println("Full Name: " + rs.getString("full_name"));
                        System.out.println("Email: " + rs.getString("email"));
                        System.out.println("Phone: " + rs.getString("phone_number"));
                        System.out.println("Visit Count: " + rs.getInt("visit_count"));
                        System.out.println("Total Spent: " + rs.getBigDecimal("total_spent"));
                    } else {
                        System.out.println("No user found with ID: " + id + " and name: " + name);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// CHANGES MADE:
// - Removed manual resource management.
// - Used try-with-resources for Connection, PreparedStatement, and ResultSet everywhere.
// - No resource leaks possible now.
