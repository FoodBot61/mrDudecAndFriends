
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    private String user_id = "1";
    int price;
    int idDish;
    int date;
    String dish;
    String msgText;
    String FirstName;
    String LastName;
    String takefoodforname;
    Dish td;
    String logvalues;

    //testvar
   List<String> ca;
    public List<String> log(Message message) throws SQLException {

        String fullmsg = message.toString();
        //System.out.println(fullmsg); //Сводка по сообщению
        msgText = message.getText();
        Pattern p = Pattern.compile("id=[0-9]+,");
        Matcher m = p.matcher(fullmsg);
        if (m.find()) {
            user_id = fullmsg.substring(m.start() + 3, m.end() - 1);
        }
        date = message.getDate();
        td = new Dish();
        FirstName = "SELECT first_name FROM user WHERE id='" + user_id + "'";
        LastName = "SELECT last_name FROM user WHERE id='" + user_id + "'";
        BD.rs = BD.stmt.executeQuery(FirstName);

        while (BD.rs.next()) {
            user_name = BD.rs.getString(1);
        }
        BD.rs = BD.stmt.executeQuery(LastName);

        while (BD.rs.next()) {
            user_secname = BD.rs.getString(1);
        }

        try {
            DishName = td.findDish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
ca=new ArrayList<String>();

        for (i = 0; i < DishName.length; i++) {
            if ((message.getText().contains(DishName[i]))) {
                takefoodforname = "SELECT * FROM `dish` WHERE dish_name ='" + DishName[i] + "'";
                BD.rs = BD.stmt.executeQuery(takefoodforname);

                while (BD.rs.next()) {
                    dish = BD.rs.getString(2);
                    price = BD.rs.getInt(5);
                    idDish = BD.rs.getInt(1);

                        ca.add(0,dish);
                      //  System.out.print(ca.get(0));

                }
                System.out.print("test"+ca.get(0));
            }
        }

return ca;
    }
    }















