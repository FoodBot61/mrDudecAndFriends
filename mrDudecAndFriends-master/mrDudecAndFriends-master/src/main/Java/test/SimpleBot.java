package test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.Blob;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import test.BD;
import static java.lang.System.exit;
import test.Logging;
/**
 * Created by User on 08.01.2017.
 */

public class SimpleBot extends TelegramLongPollingBot {
    String user_id;
    String user_name;


        public static void main(String[] args) throws IOException {
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(new SimpleBot());
            } catch (TelegramApiException e) {
                e.printStackTrace();

            }

        }




    @Override
        public String getBotUsername() {
            return "FoodBot";
        }

        @Override
        public String getBotToken() {
            return "316708819:AAEdaPqrGqRt7E7Kpg0oXosJrjcQyjm5FUY";
        }

             public void hola( Message message) throws SQLException {
               String message1 = message.toString();
                Pattern p = Pattern.compile("id=[0-9]+,");
                Matcher m = p.matcher(message1);
                if (m.find()) {
                    user_id = message1.substring(m.start() + 3, m.end() - 1);
                    System.out.println(user_id+"fsdf");
                }
                String sql = "SELECT first_name FROM `user` WHERE id='" +user_id+"' ";
                BD.rs = BD.stmt.executeQuery(sql);
                while (BD.rs.next()) {
                    user_name = BD.rs.getString(1);
                }
            }

    @Override
        public void onUpdateReceived(Update update) {
            Message message = update.getMessage();
           String cmd = message.getText();
            SimpleBot simpleBot = new SimpleBot();
            simpleBot.sshaasd(update);
            Logging log = new Logging();
            UserIntoBD us= new UserIntoBD();
            Keywords keyw = new Keywords();


                    if (message != null && message.hasText() && message.getText().contains("/")) {
                switch (cmd) {
                    case "/help":
                        sendMsg(message, "top5 - выводит топ 102000000 блюд \n help - выводит список команд \n favlist - бла бла бла ");
                        break;
                    case "/top5":
                        sendMsg(message, "gjkndgnuidfvdsvsdvsdvsdvsdvsd");
                        break;
                    case "/start":
                        try {
                            hola(message);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        sendMsg(message, "Привет,"+user_name);
                        break;
                    case "/favlist":
                        sendMsg(message, "gejgiejig");
                        break;
                    default:
                        sendMsg(message, "Нет такой команды. Для полного списка команд используйте /help");
                }
            }

        if(message.getText().equalsIgnoreCase("привет")){
            sendMsg(message, "Привет, проголодался?");
        }
        try {
            us.usrintbd(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
          log.log(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {;
          String s[] = keyw.findShaurma(message);
            for (int i=0;i<s.length;i++) {
               sendMsg(message,(i+1 +  " " + s[i]));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private  void sendMsg(Message message, String text) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId().toString());
            //sendMessage.setReplyToMessageId(message.getMessageId()); // Пересылка сообщений
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


public void sshaasd(Update update){
    BD bd = new BD();
    bd.BDsher();

    Message message = update.getMessage();
    String msgText=message.getText(); // ПОИСК БЛЮДА ПО НАЗВАНИЮ
//    System.out.println(message.getText()); Может нужно для log

    if(message.getText().equalsIgnoreCase(msgText))
    {
        try {
            String sql = "SELECT * FROM `dish` WHERE dish_name='"+msgText+"' ";
            BD.rs = BD.stmt.executeQuery(sql);
            while (BD.rs.next()) {

                String count = BD.rs.getString(1) + " " + BD.rs.getString(2) + " " + BD.rs.getString(4) + " " + BD.rs.getString(5) + " " + BD.rs.getString(6);
                // java.sql.Blob fsd = BD.rs.getBlob(3);
                System.out.println("Total number of books in the table : " + count);
                sendMsg(message, count);

            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
else {

//        System.out.println(message.getText()+"sosizui");    ОБРАТОКА ОШИБКИ 1: НЕ ПРОШЕЛ ЗАПРОС
    }

    }

}











