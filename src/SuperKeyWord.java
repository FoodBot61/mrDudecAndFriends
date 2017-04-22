
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
    private String[] Dishes;
    private String[] Keywords;
    private String Keyword;
    private List ListofDish = new ArrayList();
    private List ListofKeywords = new ArrayList();

    public String[] findDishKW(Message message) throws SQLException, NullPointerException {

        String sql1 = "SELECT word FROM `key_words`";
        BD.rs = BD.stmt.executeQuery(sql1);
        while (BD.rs.next()) {
            ListofKeywords.add(BD.rs.getString(1));
        }
        Keywords = (String[]) ListofKeywords.toArray(new String[ListofDish.size()]);
        for (int i = 0; i < ListofKeywords.size(); i++) {
            Keyword = Keywords[i];
            String msgText = message.getText();
            if ((msgText.contains("Я не хочу") == false) && (msgText.contains(Keyword))) {
                String sql = "SELECT dish.dish_name,res.name FROM `key_words`,`dish`,`dishes`,`res` WHERE  word='"+Keyword+"' and dish.id=dishes.id_dish and key_words.id=dishes.id_keyword and res.id=dishes.id_res";
                BD.rs = BD.stmt.executeQuery(sql);
                while (BD.rs.next()) {
                    String dish=BD.rs.getString(1)+" из "+BD.rs.getString(2);
                    ListofDish.add(dish);
                }
                Dishes = (String[]) ListofDish.toArray(new String[ListofDish.size()]);
            }
        }
        return Dishes;
    }
}
