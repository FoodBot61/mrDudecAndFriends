package sd;

import org.telegram.telegrambots.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 31.01.2017.
 */
public class GetPhoneNumber {
String msgText;
String phone;

    public String getPhoneNumb(Message message)
    {
        msgText=message.getText();
        Pattern p = Pattern.compile("89.[0-9]{8}");
        Matcher m = p.matcher(msgText);
        if(m.find())
        {
            phone=msgText.substring(m.end()-11,m.end());
            System.out.print(phone);
        }
        return phone;
    }
}
