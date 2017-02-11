
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
    List ListofDish = new ArrayList();
    List ListofKeywords = new ArrayList();
    List ListofDishforException = new ArrayList();
    String DishQuery;
    String Dishes1[];
    public String[] findDishKW(Message message) throws SQLException,NullPointerException{
        String sql1="SELECT word FROM ` key_words` WHERE 1";
        BD.rs= BD.stmt.executeQuery(sql1);
        while (BD.rs.next()) {
            ListofKeywords.add(BD.rs.getString(1));
        }
        DishQuery= "SELECT dish_name FROM `dish` WHERE 1";
        BD.rs = BD.stmt.executeQuery(DishQuery);
        while (BD.rs.next()) {
            ListofDishforException.add(BD.rs.getString(1));
        }
        Dishes1=(String[]) ListofDishforException.toArray(new String[ListofDish.size()]);
        Keywords = (String[])  ListofKeywords.toArray(new String[ListofDish.size()]);
        for (int i = 0; i <  ListofKeywords.size(); i++) {
            Keyword = Keywords[i];//КЛЮЧЕВЫЕ СЛОВА
            Dish = Dishes1[i];
            String msgText = message.getText();
            if ((msgText.replace(msgText.charAt(0),String.valueOf(msgText.charAt(0)).toLowerCase().charAt(0)).contains(Keyword)
                    &&(msgText.contains("Я не хочу "))==false)
                    &&(msgText.contains(Dishes1[i])==false))
            {
                String sql = "SELECT dish_name FROM ` key_words`,`dish` WHERE dish.id_keyword=` key_words`.`id` and word='" + Keyword + "'";// АДРЕС CLIENT
                BD.rs = BD.stmt.executeQuery(sql);
                while (BD.rs.next()) {
                    ListofDish.add(BD.rs.getString(1));
                }

                Dishes = (String[]) ListofDish.toArray(new String[ListofDish.size()]);
            }
        }
        for (int i1 = 0; i1 < ListofDish.size(); i1++) {
                Dish = Dishes[i1];
            }
        for (int i = 0; i < Dishes.length; i++) {
            System.out.println (Dishes[i]);//БЛЮДА ПО КЛЮЧЕВЫМ СЛОВАМ(sup)
        }
        return Dishes;
    }
}
