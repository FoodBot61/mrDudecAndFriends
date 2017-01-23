package sd;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by user on 22.01.2017.
 */
public class SuperKeyWord {

    String[] p;
    String p1;
    String[] f;
    String f1;
    List rowValues = new ArrayList();
    List rowValues1 = new ArrayList();

    public String[] findShaurma(Message message) throws SQLException,NullPointerException{
        String sql1="SELECT word FROM ` key_words` WHERE 1";
        BD.rs= BD.stmt.executeQuery(sql1);
        while (BD.rs.next()) {
            rowValues1.add(BD.rs.getString(1));
        }
        f = (String[]) rowValues1.toArray(new String[rowValues.size()]);
        for (int i = 0; i < rowValues1.size(); i++) {
            f1 = f[i];//КЛЮЧЕВЫЕ СЛОВА
            String msgText = message.getText();
            if (msgText.toLowerCase().contains(f1)) {
                String sql = "SELECT dish_name FROM ` key_words`,`dish` WHERE dish.id_keyword=` key_words`.`id` and word='" + f1 + "'";// АДРЕС CLIENT
                BD.rs = BD.stmt.executeQuery(sql);
                    while (BD.rs.next()) {
                        rowValues.add(BD.rs.getString(1));
                    }
        // You can then put this back into an array if necessary
                    p = (String[]) rowValues.toArray(new String[rowValues.size()]);
            }
            for (int i1 = 0; i1 < rowValues.size(); i1++) {
                p1 = p[i1];
                System.out.println (p[i1]);//БЛЮДА ПО КЛЮЧЕВЫМ СЛОВАМ(sup)
            }

        }
        return p;
    }
}
