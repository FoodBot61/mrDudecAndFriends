package test;
import java.sql.*;


import static java.sql.DriverManager.getConnection;


/**
 * Created by User on 12.01.2017.
 */
public class BD {
    private static final String url = "jdbc:mysql://localhost/foodbot?characterEncoding=Cp1251";
    private static final String user = "root";
    private static final String password = "";

    // JDBC variables for opening and managing connection
    private static Connection con;
    static Statement stmt;
    static ResultSet rs;

    public static void main(String args[]) {
BD bd = new BD();
        bd.BDsher();
    }
    public void BDsher()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
//
        try {


//             opening database connection to MySQL server
            con = DriverManager.getConnection(url, user, password);
            if (con != null) {
                System.out.println("Лучше бы на повара пошёл");
            } else {
                System.out.println("А нет!");
            }
            // getting Statement object to execute query
        stmt = con.createStatement();
//
//             //executing SELECT query
//            String sql;
//            sql = "SELECT dish_name FROM dish WHERE id IN (1,3)";rs = stmt.executeQuery(sql);
//            while (rs.next()) {
//                String count = rs.getString(1);
//                System.out.println("Total number of books in the table : " + count);
//            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
//        finally {
//            //close connection ,stmt and resultset here
//            try {
//                con.close();
//            } catch (SQLException se) { /*can't do anything */ }
//            try {
//                stmt.close();
//            } catch (SQLException se) { /*can't do anything */ }
//            try {
//                rs.close();
//            } catch (SQLException se) { /*can't do anything */ }
//
//
//        }
    }

}




