import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Update {

    public void updateUser(int userId, int choice, Object newValue) {
        // CHANGED: Use try-with-resources for Connection
        try (Connection con = Dbconnection.getConnection()) {

            // CHANGED: Use try-with-resources for PreparedStatement and ResultSet (check user)
            String checkQuery = "SELECT * FROM bakery_users WHERE user_id = ?";
            try (PreparedStatement checkId = con.prepareStatement(checkQuery)) {
                checkId.setInt(1, userId);
                try (ResultSet idResult = checkId.executeQuery()) {
                    if (!idResult.next()) {
                        System.out.println("UserId does not exist for Updation :" + userId);
                        return;
                    }
                }
            }

            String column;
            switch (choice) {
                case 1: column = "full_name"; break;
                case 2: column = "email"; break;
                case 3: column = "phone_number"; break;
                case 4: column = "total_spent"; break;
                default:
                    System.out.println("Invalid choice: " + choice);
                    return;
            }

            // CHANGED: Use try-with-resources for update PreparedStatement
            String sql = "UPDATE bakery_users SET " + column + "=? WHERE user_id=?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                switch (choice) {
                    case 1:
                    case 2:
                    case 3:
                        pstmt.setString(1, (String) newValue);
                        break;
                    case 4:
                        if (newValue instanceof Double) {
                            pstmt.setDouble(1, (Double) newValue);
                        } else if (newValue instanceof String) {
                            try {
                                double numValue = Double.parseDouble((String) newValue);
                                pstmt.setDouble(1, numValue);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number format: " + newValue);
                                return;
                            }
                        } else {
                            System.out.println("Unsupported type for total_spent: " + newValue.getClass());
                            return;
                        }
                        break;
                }
                pstmt.setInt(2, userId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("User " + userId + " updated successfully!");
                } else {
                    System.out.println("No user found with ID: " + userId);
                }
            }

        } catch (Exception e) {
            System.err.println("Error updating user " + userId);
            e.printStackTrace();
        }
    }
}

// CHANGES MADE:
// - Used try-with-resources for Connection, PreparedStatement, and ResultSet everywhere.
// - No manual close() needed. No resource leaks.
