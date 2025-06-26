import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Delete {

    public void deleteUser(int id) {
        // CHANGED: Use try-with-resources for Connection
        try (Connection con = Dbconnection.getConnection()) {

            // CHANGED: Use try-with-resources for PreparedStatement and ResultSet (check user)
            String checkQuery = "SELECT * FROM bakery_users WHERE user_id = ?";
            try (PreparedStatement checkId = con.prepareStatement(checkQuery)) {
                checkId.setInt(1, id);
                try (ResultSet idResult = checkId.executeQuery()) {
                    if (!idResult.next()) {
                        System.out.println("UserId does not exist for deletion for user_id :" + id);
                        return;
                    }
                }
            }

            // CHANGED: Use try-with-resources for delete PreparedStatement
            String deleteQuery = "DELETE FROM bakery_users WHERE user_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Entry for userId-> " + id + " has been deleted");
                } else {
                    System.out.println("Failed to delete the entry");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// CHANGES MADE:
// - Used try-with-resources for Connection, PreparedStatement, and ResultSet everywhere.
// - No manual close() needed. No resource leaks.
