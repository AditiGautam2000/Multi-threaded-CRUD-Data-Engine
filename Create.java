import java.sql.Connection;
import java.sql.PreparedStatement;

class Create {

    public void createUser(int userId, String fullName, String email, String phone, int visitCount, double totalSpent) {
        // CHANGED: Use try-with-resources for Connection and PreparedStatement
        String query = "INSERT INTO bakery_users(user_id, full_name, email, phone_number, visit_count, total_spent) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Dbconnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            System.out.print("Details of user entered. Creating an entry of this user ");

            pstmt.setInt(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setInt(5, visitCount);
            pstmt.setDouble(6, totalSpent);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Entry created successfully! :" + userId);
            } else {
                System.out.println("Failed to create entry.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// CHANGES MADE:
// - Used try-with-resources for both Connection and PreparedStatement.
// - No manual close() needed. No resource leaks.
