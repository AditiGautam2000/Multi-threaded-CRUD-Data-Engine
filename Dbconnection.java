
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 
class Dbconnection{
public static Connection getConnection() throws SQLException {
    Connection con;

        String url="jdbc:mysql://localhost:3306/bakery";
        String username="root";
        String password="mysql";

        return DriverManager.getConnection(url, username, password); //1
        
       
    }
    } 