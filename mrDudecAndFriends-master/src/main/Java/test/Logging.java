package test;
import java.sql.SQLException;
import java.util.regex.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import javax.sql.rowset.serial.SQLInputImpl;
import java.io.FileWriter;
import java.io.*;
import java.io.File;

/**
 * Created by user on 15.01.2017.
 */
public class Logging extends SimpleBot {

    public static void main(String[] args) throws IOException {

    }
    public void log(Message message) throws SQLException {

        int idDish=1;
        int price=1;
        BD bd = new BD();
        bd.BDsher();
        String msgText = message.getText();
         String dishname="SELECT * FROM dish WHERE dish_name = '"+msgText+"'";
        System.out.println(dishname);
        System.out.println(msgText);

        BD.rs = BD.stmt.executeQuery(dishname);
        while (BD.rs.next()) {
             price = BD.rs.getInt(5);
            idDish = BD.rs.getInt(9);
        }

    try {

        String sql = "INSERT INTO log " +
                "SET" +
                " user_id = 1," +
                " user_name = 'Sasha'," +
                " user_secname = 'Kulik', " +
                " `text_msg` = '" + msgText + "'," +
                " `date_msg` = '1990-03-10'," +
                " `price` =  '" + price + "', " +
                " `amount` = 1," +
                " `id_res` = 1," +
                " `adress` = 'Puskina'," +
                " `id_dish` = '"+idDish+"' ";
           BD.stmt.executeUpdate(sql);


        if (BD.stmt != null) {
            System.out.println("eeeeee rock" + msgText);

        } else {

            System.out.println("=-(");

        }
    } catch (SQLException sqlEx) {
        sqlEx.printStackTrace();
    }
}
        }



