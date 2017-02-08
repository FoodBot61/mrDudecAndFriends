package sd;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import jersey.repackaged.com.google.common.collect.Maps;
import org.json.JSONObject;
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
    String user_name;
    int i;
    String Keywords[];
    String[] DishName;
    String DishQuery;
    boolean forKeyWords;
    String Dishes = " ";
    int TotalPrice;
    String TotalDish;
    GeoApiContext context;
    String address;
    String userPhone;
    String takePhone;
    JsonR jsonR;
    GetPhoneNumber ph;
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

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void getPhoneAndAddress(Message message) {

        if (TotalDish != null) {
            userPhone = ph.getPhoneNumb(message);
            if (message.getText().equals(userPhone)) {
                sendMsg(message, "Введите адрес, чтобы курьер знал куда ехать ");
                takePhone = message.getText();
            }


            if ((takePhone != null) & (message.getText().matches("[0-9]{0,4}[^0-9]{0,2}[а-я].+[0-9]{1,4}"))) {
                context = new GeoApiContext().setApiKey("AIzaSyAg5cKfRFcLIxAUuPSs8IFXX5dnbH844uw");
                address = jsonR.URLmaker(message);
                sendMsg(message, address);
                sendMsg(message, "Ваш телефон :" + takePhone + "\n"
                        + "Ваш адрес :" + address + "\n"
                        + "Если данные указаны верно, введите 'Да'\n"
                        + "В случае ошибки введите 'Нет'");
            }

        }
    }

    public void hello(Message message) {
        String message1 = message.toString();
        Pattern p = Pattern.compile("firstName='[^0-9]+'+,+.l");
        Matcher m = p.matcher(message1);
        if (m.find()) {
            user_name = message1.substring(m.start() + 11, m.end() - 4);
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String cmd = message.getText();
        Logging log = new Logging();
        UserIntoBD us = new UserIntoBD();
        SuperKeyWord keyw = new SuperKeyWord();
        Dish td = new Dish();
        ph = new GetPhoneNumber();
        jsonR = new JsonR();
        if (message != null && message.hasText() && message.getText().contains("/")) {
            switch (cmd) {
                case "/help":
                    sendMsg(message, "Cписок команд:  \n\n" +
                            "/top5 - выводит топ 102000000 блюд" +
                            "\n/help - выводит список команд" +
                            "\n/favlist - бла бла бла \n\n" +
                            "\t Описание работы с ботом \n\n" +
                            "Для начала заказа введите ключевые слова или названия блюд." +
                            "\nДля того чтобы заверишь заказ введите STOP.\nЧтобы убрать блюдо из общей корзины");
                    break;
                case "/top5":
                    sendMsg(message, "gjkndgnuidfvdsvsdvsdvsdvsdvsd");
                    break;
                case "/start":
                    hello(message);
                    sendMsg(message, "Привет," + user_name);
                    sendMsg(message, "Cписок команд:  \n\n" +
                            "/top5 - выводит топ 102000000 блюд" +
                            "\n/help - выводит список команд" +
                            "\n/favlist - бла бла бла \n\n" +
                            "\t Описание работы с ботом \n\n" +
                            "Для начала заказа введите ключевые слова или названия блюд." +
                            "\nДля того чтобы заверишь заказ введите STOP.\nЧтобы убрать блюдо из общей корзины");
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
            DishName = td.TestDish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (message.getText().contains("Я не хочу ")) {
            // УДАЛЕНИЕ БЛЮДА ИЗ ОБЩЕГО СПИСКА
            for (i = 0; i < DishName.length; i++) {
                if ((Dishes.contains(DishName[i])) & (message.getText().equals("Я не хочу " + DishName[i]))) {
                    String kol = message.getText().replace("Я не хочу ", "");

                    String reworkprice = "SELECT price FROM `dish` WHERE dish_name ='" + DishName[i] + "'";
                    try {
                        BD.rs = BD.stmt.executeQuery(reworkprice);
                        while (BD.rs.next()) {
                            Dishes = Dishes.replaceFirst(kol, "");
                            TotalPrice = TotalPrice - BD.rs.getInt(1);
                            sendMsg(message, "Ваш заказ : " + Dishes + " " + "на сумму" + TotalPrice + " rub");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            for (i = 0; i < DishName.length; i++) {
                if ((message.getText().contains(DishName[i]))) {
                    DishQuery = "SELECT * FROM `dish` WHERE dish_name ='" + DishName[i] + "'";
                    try {
                        BD.rs = BD.stmt.executeQuery(DishQuery);
                        while (BD.rs.next()) {
                            sendMsg(message, "\n" + "\n" + "\n" + "Название : " + BD.rs.getString(2) + "\n" + "Описание : " + BD.rs.getString(4) + "\n" + "Цена :" + BD.rs.getInt(5) + " rub " + "\n" + "Ингридиенты  :" + BD.rs.getString(6) + "\n" + "Фото : " + BD.rs.getString(3));
                            Dishes = BD.rs.getString(2) + " | " + Dishes;
                            TotalDish = Dishes;
                            TotalPrice = BD.rs.getInt(5) + TotalPrice;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    sendMsg(message, "Итоговая стоимость = " + TotalPrice + " rub");// отсюда и пляши, дядя
                    sendMsg(message, "Итоговый заказ : " + Dishes);


                    forKeyWords = true;

                }
            }
        }
            if (message.getText().equals("STOP")) {
                if (Dishes != null && TotalPrice != 0) {
                    sendMsg(message, "Ваш заказ : " + Dishes + " " + "на сумму" + TotalPrice + " rub");
                    TotalPrice = 0;
                    Dishes = "";
                    sendMsg(message, "Введите номер телефона, чтобы курьер смог связаться с вами");
                } else {
                    sendMsg(message, "Закажите что-нибудь.Надо поесть");
                }
            }

        getPhoneAndAddress(message);


            if ((takePhone!=null)&&(address!=null)){
            String gol = message.getText();
                switch (gol)
                {
                    case "Да" :
                        sendMsg(message, "Все нормально");
                        takePhone = null;
                        try {

                            String whatafa = jsonR.DistanseBe(address);
                            sendMsg(message,"Ближайщий ресторан к вам находится на "+whatafa.replace("_"," ").replace(".","")+
                                    "\nПримерное время доставки "+whatafa.replaceAll("[A-я]",""));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "Нет" :
                        sendMsg(message, "????????");
                        sendMsg(message,"\nВведите данные повторно");
                        getPhoneAndAddress(message);
                        break;

                }
            }


        try {
                log.log(message);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!forKeyWords) {
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
            forKeyWords = false;
        }

}











