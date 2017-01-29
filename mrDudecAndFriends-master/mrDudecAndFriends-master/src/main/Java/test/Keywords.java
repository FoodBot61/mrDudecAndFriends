package test;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import test.BD;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 21.01.2017.
 */
public class Keywords extends UserIntoBD {
    final String shaurma = "шаурма";

    String[] l;
    String l1;

    List rowValues = new ArrayList();
    public String[] findShaurma(Message message) throws SQLException {

        String msgText = message.getText();
        if (msgText.toLowerCase().contains(shaurma)) {

            String sql = "SELECT dish_name FROM ` key_words`,`dish` WHERE dish.id_keyword=` key_words`.`id` and word='" + shaurma + "'";// АДРЕС CLIENT
            Keywords.rs = Keywords.stmt.executeQuery(sql);

            while (rs.next()) {
                rowValues.add(rs.getString(1));
            }
// You can then put this back into an array if necessary
            l = (String[]) rowValues.toArray(new String[rowValues.size()]);

        }
        for (int i=0;i<rowValues.size();i++)
        {
            //System.out.println (l[i]);
           l1=l[1];


        }
        //System.out.println (l1);
        return l;
    }

}


