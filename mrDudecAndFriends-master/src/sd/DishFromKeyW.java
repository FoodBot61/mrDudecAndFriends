package sd;

import org.telegram.telegrambots.api.objects.Message;

import java.sql.SQLException;

/**
 * Created by user on 22.01.2017.
 */
public class DishFromKeyW extends SuperKeyWord {


    public String howver(Message message) throws SQLException {


        {
            SuperKeyWord keyw = new SuperKeyWord();
            String s[] = new String[0];
            try {
                s = keyw.findShaurma(message);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < s.length; i++) {
                String pull= (i + 1 + " " + s[i]);
                System.out.print(String.valueOf(i));
                if(String.valueOf(i).contains(message.getText())){
                    System.out.print("sosi ");
                }
            }


        }
        return null;
    }
}