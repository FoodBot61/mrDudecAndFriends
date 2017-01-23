package sd;
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

    public void BDsher()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        try {
            con = DriverManager.getConnection(url, user, password);
            if (con != null) {
                System.out.println("Лучше бы на повара пошёл");
            } else {
                System.out.println("А нет!");
            }

        stmt = con.createStatement();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}




