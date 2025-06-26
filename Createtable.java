import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
 
import java.util.*;
class CreateTable{
public static void main(String[] args) {

    try{
        String url="jdbc:mysql://localhost:3306";
        String username="root";
        String password="mysql";

        Connection con= DriverManager.getConnection(url, username, password); //1
        Statement st=con.createStatement(); //2
        st.executeUpdate("create database if not exists bakery");  //3
        st.executeUpdate("use bakery");
        st.executeUpdate("create table if not exists bakery_users ("+"user_id int primary key,"+"full_name varchar(50),"+"email varchar(100),"+
        "phone_number varchar(50),"+"visit_count int,"+"total_spent decimal(40,2))");

        String insertSQL="Insert into bakery_users(user_id,full_name,email,phone_number,visit_count,total_spent) values(?,?,?,?,?,?)";
        PreparedStatement pstmt=con.prepareStatement(insertSQL);

        BufferedReader br=new BufferedReader(new FileReader("bakery_users_indian_phone.csv"));

        String line;
        br.readLine(); // Skip header
        
        while((line=br.readLine())!=null){
            String [] values=line.split(",",-1);
            if(values.length<6) continue;
            
            pstmt.setInt(1,Integer.parseInt(values[0]));
            pstmt.setString(2,values[1]);
            pstmt.setString(3, values[2]);
            pstmt.setString(4, values[3]);
            pstmt.setInt(5, Integer.parseInt(values[4]));
            pstmt.setBigDecimal(6, new java.math.BigDecimal(values[5]));

            pstmt.execute();

        }
        br.close(); //4
        System.out.println("Data inserted successfully!");

        
    }
    catch(Exception e){
        e.printStackTrace();
    }
    
}
}