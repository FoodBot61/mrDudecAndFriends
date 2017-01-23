package sd;
import java.sql.ResultSet;
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
    private String user_name;
    private String user_secname;
    private String user_id="1";
    public void log(Message message) throws SQLException {
            int idDish = 1;
            int price = 0;
            String fullmsg = message.toString();
            System.out.println(fullmsg);
            String msgText = message.getText();
            String adress = "SELECT adress FROM user WHERE id=104730502";// АДРЕС CLIENT
            Pattern p = Pattern.compile("id=[0-9]+,");
            Matcher m = p.matcher(fullmsg);
            if (m.find()) {
                user_id = fullmsg.substring(m.start() + 3, m.end() - 1);
            }

            System.out.println(user_id);
            int date = message.getDate();
            String dishname = "SELECT * FROM dish WHERE dish_name = '" + msgText + "'";
            System.out.println(msgText);
            BD.rs = BD.stmt.executeQuery(dishname);
            while (BD.rs.next()) {
                price = BD.rs.getInt(5);
                idDish = BD.rs.getInt(1);
                }
        String fn = "SELECT first_name FROM user WHERE id='"+user_id+"'";
        String ln = "SELECT last_name FROM user WHERE id='"+user_id+"'";
            BD.rs=BD.stmt.executeQuery(fn);
            while (BD.rs.next()) {
            user_name = BD.rs.getString(1);
        }
            BD.rs=BD.stmt.executeQuery(ln);
            while (BD.rs.next()) {
                user_secname=BD.rs.getString(1);
            }
            try {
                    String sql = "INSERT INTO log " +
                            "SET" +
                            " user_id = '" + user_id + "'," +
                            " user_name = '" + user_name + "'," +
                            " user_secname = '" + user_secname + "', " +
                            " `text_msg` = '" + msgText + "'," +
                            " `date_msg` = '" + String.valueOf(date) + "'," +
                            " `price` =  '" + price + "', " +
                            " `amount` = 1," +
                            " `id_res` = 1," +
                            " `adress` = '" + adress + "'," +
                            " `id_dish` = '" + idDish + "' ";
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



