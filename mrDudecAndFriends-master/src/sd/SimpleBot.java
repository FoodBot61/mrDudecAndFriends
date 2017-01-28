package sd;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * Created by User on 08.01.2017.
 */


public class SimpleBot extends TelegramLongPollingBot {
    String user_id;
    String user_name;
    int i;
    String Keywords[];
    String[] DishName;

    boolean a;
    //ТЕСТ ПЕРЕМЕННЫЕ
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

    public void hello(Message message) throws SQLException {
        String message1 = message.toString();
        Pattern p = Pattern.compile("id=[0-9]+,");
        Matcher m = p.matcher(message1);
        if (m.find()) {
            user_id = message1.substring(m.start() + 3, m.end() - 1);
            System.out.println(user_id + "fsdf");
        }
        String sql = "SELECT first_name FROM `user` WHERE id='" + user_id + "' ";
        BD.rs = BD.stmt.executeQuery(sql);
        while (BD.rs.next()) {
            user_name = BD.rs.getString(1);
        }
    }


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String cmd = message.getText();
        Logging log = new Logging();
        UserIntoBD us = new UserIntoBD();
        SuperKeyWord keyw = new SuperKeyWord();
        Dish td = new Dish();
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
                        hello(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    sendMsg(message, "Привет," + user_name);
                    break;
                case "/favlist":
                    sendMsg(message, "gejgiejig");
                    break;
                default:
                    sendMsg(message, "Нет такой команды. Для полного списка команд используйте /help");
            }
        }

        if (message.getText().equalsIgnoreCase("привет")) {
            sendMsg(message, "Привет, проголодался?");
            sendMsg(message, "http://minionomaniya.ru/wp-content/uploads/2016/01/%D0%9A%D0%B5%D0%B2%D0%B8%D0%BD.jpg");
        }

        try {
            us.usrintbd(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            DishName = td.TestDish(message);


        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (i = 0; i < DishName.length; i++) {
            if ((message.getText().contains(DishName[i]))) {

                String takefoodforname = "SELECT * FROM `dish` WHERE dish_name ='"+DishName[i]+"'";

                try {
                    BD.rs=BD.stmt.executeQuery(takefoodforname);
                    while (BD.rs.next())
                    {
                        sendMsg(message,"Название : " +BD.rs.getString(2) + "\n"+  "Описание : " +  BD.rs.getString(4) +"\n" + "Цена :"  + BD.rs.getString(5) +"\n" + "Ингридиенты :" +BD.rs.getString(6));

                    }
                    try {
                        log.log(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                a=true;
            }

        }

        if (!a) {

            try {

                Keywords = keyw.findShaurma(message);
                if (Keywords == null) {
                    sendMsg(message, "Ну что ты,бабуин введи что нибудь нормальное");

                } else {
                    for (i = 0; i < Keywords.length; i++) {
                        sendMsg(message, (i + 1 + " " + Keywords[i]));

                    }
                    sendMsg(message, "Введите название блюда");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        a=false;
    }







    private  void sendMsg(Message message, String text) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(false);
            sendMessage.setChatId(message.getChatId().toString());
            //sendMessage.setReplyToMessageId(message.getMessageId()); // Пересылка сообщений
            sendMessage.setText(text);
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }



}











