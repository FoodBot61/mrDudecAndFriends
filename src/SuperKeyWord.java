
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
public class SuperKeyWord {
    String[] Dishes;
    String Dish;
    String[] Keywords;
    String Keyword;
    List ListofDish = new ArrayList();
    List ListofKeywords = new ArrayList();

    public String[] findDishKW(Message message) throws SQLException, NullPointerException {
        String sql1 = "SELECT word FROM `key_words` WHERE 1";
        BD.rs = BD.stmt.executeQuery(sql1);
        while (BD.rs.next()) {
            ListofKeywords.add(BD.rs.getString(1));
        }
        Keywords = (String[]) ListofKeywords.toArray(new String[ListofDish.size()]);
        for (int i = 0; i < ListofKeywords.size(); i++) {
            Keyword = Keywords[i];//КЛЮЧЕВЫЕ СЛОВА
            String msgText = message.getText();
            if ((msgText.contains("Я не хочу") == false) && (msgText.contains(Keyword))) {
                String sql = "SELECT dish_name FROM `key_words`,`dish` WHERE dish.id_keyword=`key_words`.`id` and word='" + Keyword + "'";// АДРЕС CLIENT
                BD.rs = BD.stmt.executeQuery(sql);
                while (BD.rs.next()) {
                    ListofDish.add(BD.rs.getString(1));
                }
                Dishes = (String[]) ListofDish.toArray(new String[ListofDish.size()]);
            }
        }
        for (int i1 = 0; i1 < ListofDish.size(); i1++)
        {
            Dish = Dishes[i1];
        }
        return Dishes;
    }
}
