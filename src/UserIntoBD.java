
import org.telegram.telegrambots.api.objects.Message;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 19.01.2017.
 */
public class UserIntoBD extends BD {
    private String user_name;
    private String user_secname;
    private String user_id;
    private String queryUser;

    public void usrintbd(Message message) throws SQLException {
        BDsher();
        String fullmsg = message.toString();
        findUserId(fullmsg);
        findUserName(fullmsg);
        findUserSecName(fullmsg);
        queryUser = "INSERT IGNORE INTO user " +
                "SET" +
                " id = '" + user_id + "'," +
                "first_name = '" + user_name + "'," +
                "last_name = '" + user_secname + "'";
        try {
            stmt.executeUpdate(queryUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void findUserId(String fullmsg) {
        Pattern p = Pattern.compile("id=[0-9]+,");
        Matcher m = p.matcher(fullmsg);
        if (m.find()) {
            user_id = fullmsg.substring(m.start() + 3, m.end() - 1);
        }
    }

    public void findUserName(String fullmsg) {
        Pattern p = Pattern.compile("firstName='[^0-9]+'+,+.l");
        Matcher m = p.matcher(fullmsg);
        if (m.find()) {
            user_name = fullmsg.substring(m.start() + 11, m.end() - 4);
        }
    }

    public void findUserSecName(String fullmsg) {
        Pattern p = Pattern.compile("lastName='[^0-9]+'+,+.u");
        Matcher m = p.matcher(fullmsg);
        if (m.find()) {
            user_secname = fullmsg.substring(m.start() + 10, m.end() - 4);
        }
    }
}
