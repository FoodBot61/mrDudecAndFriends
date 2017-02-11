
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by user on 22.01.2017.
 */
public class SuperKeyWord  {
    String[] Dishes;
    String Dish;
    String[] Keywords;
    String Keyword;
    List rowValues = new ArrayList();
    List rowValues1 = new ArrayList();

    public String[] findDishKW(Message message) throws SQLException,NullPointerException{
        String sql1="SELECT word FROM ` key_words` WHERE 1";
        BD.rs= BD.stmt.executeQuery(sql1);
        while (BD.rs.next()) {
            rowValues1.add(BD.rs.getString(1));
        }
        Keywords = (String[]) rowValues1.toArray(new String[rowValues.size()]);
        for (int i = 0; i < rowValues1.size(); i++) {
            Keyword = Keywords[i];//КЛЮЧЕВЫЕ СЛОВА
            String msgText = message.getText();
            if ((msgText.toLowerCase().contains(Keyword)&&(msgText.contains("Я не хочу "))==false)) {  //СООБЩЕНИЕ СОДЕРЖИТ ИЛИ РАВНО КЛЮЧУ
                String sql = "SELECT dish_name FROM ` key_words`,`dish` WHERE dish.id_keyword=` key_words`.`id` and word='" + Keyword + "'";// АДРЕС CLIENT
                BD.rs = BD.stmt.executeQuery(sql);
                while (BD.rs.next()) {
                    rowValues.add(BD.rs.getString(1));
                }
                // You can then put this back into an array if necessary
                Dishes = (String[]) rowValues.toArray(new String[rowValues.size()]);
            }
        }
        for (int i1 = 0; i1 < rowValues.size(); i1++) {
                Dish = Dishes[i1];
            }
        for (int i = 0; i < Dishes.length; i++) {
            System.out.println (Dishes[i]);//БЛЮДА ПО КЛЮЧЕВЫМ СЛОВАМ(sup)
        }
        return Dishes;
    }
}
