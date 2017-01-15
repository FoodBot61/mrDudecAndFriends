package test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Blob;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
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

/**
 * Created by User on 08.01.2017.
 */

public class SimpleBot extends TelegramLongPollingBot {



        public static void main(String[] args) {


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

    @Override
        public void onUpdateReceived(Update update) {
            Message message = update.getMessage();
           String cmd = message.getText();
SimpleBot simpleBot = new SimpleBot();
        simpleBot.sshaasd(update);

            if (message != null && message.hasText()) {

                switch (cmd) {
                    case "привет":
                        sendMsg(message, "Привет, проголодался?");
                        break;
                    case "/help":
                        sendMsg(message, "top5 - выводит топ 102000000 блюд \n help - выводит список команд \n favlist - бла бла бла ");
                        break;
                    case "/top5":
                        sendMsg(message, "gjkndgnuidfvdsvsdvsdvsdvsdvsd");
                        break;
                    case "/favlist":
                        sendMsg(message, "gejgiejig");
                        break;
                    default:
                        sendMsg(message, "Я не знаю что ответить на это");
                }
            }
        }



    private void sendMsg(Message message, String text) {
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
    String sf="Пицца";
    Message message = update.getMessage();
try{
   String sql = "SELECT * FROM `dish` WHERE `dish_name`='Cуп харчо'";
    BD.rs = BD.stmt.executeQuery(sql);
    while (BD.rs.next()) {
        String count = BD.rs.getString(1) + " "+ BD.rs.getString(2) + " "+ BD.rs.getString(4)+ " "+BD.rs.getString(5)+ " "+BD.rs.getString(6);
       // java.sql.Blob fsd = BD.rs.getBlob(3);
        System.out.println("Total number of books in the table : " + count);
        sendMsg(message,count);

    }
    } catch (SQLException sqlEx) {
        sqlEx.printStackTrace();
    }


    }
}










