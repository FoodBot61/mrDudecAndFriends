import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.maps.GeoApiContext;
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
    private int i;
    private String DishesonKW[];
    private String[] DishName;
    private String DishQuery;
    private String TotalDishQuery;
    private boolean forKeyWords;
    private String Dish = " ";
    private GeoApiContext context;
    private String takePhone;
    private int Price;
    private String userPhone;
    private String Phone;
    private String address;
    private String TotalDish = " ";
    private int TotalPrice;
    private JsonR jsonR;
    private String userIdRest;
    private String user_name;
    private String user_secname;
    private String user_id;
    private int DateForLog;
    private int PriceForLog;
    private int IdDishForLog;
    private String UserIdRestForLog;
    private String DishForLog;
    private String TotalDishForRest;
    private int TotalPriceForRest;
    private Boolean letmeError;
    int OrderNumber = 1;
    private String TotalDishForLog;
    private int TotalPriceForOrder;
    private String TotalDishForOrder = " ";
    private int userIdFromDB;

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
        user_id = jsonR.takeUserIdFromMessage(message);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(user_id);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getPhoneNumb(Message message) {
        String msgText;
        String phone = null;
        msgText = message.getText();
        Pattern p = Pattern.compile("89.[0-9]{8}");
        Matcher m = p.matcher(msgText);
        if (m.find()) {
            phone = msgText.substring(m.end() - 11, m.end());
        }
        return phone;
    }

    private void sendMsgToRest(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(userIdRest);
        sendMessage.setChatId(userIdRest);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

public void takeUserIdFromDB(Message message){      //получение id пользователя по id чата телеги
    String takeUserIdQuery="SELECT user_id FROM orders WHERE user_id='"+message.getChatId()+"'";
    try {
        BD.rs=BD.stmt.executeQuery(takeUserIdQuery);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    try {
        while(BD.rs.next())
        {
            userIdFromDB =BD.rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void takeOrderForUser(Message message){ //блюда из общего заказа
    try {
        TotalDishQuery = "SELECT dish_name,price FROM orders WHERE user_id='" + message.getChatId()+ "'";
        BD.rs = BD.stmt.executeQuery(TotalDishQuery);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    try {
        while (BD.rs.next()) {
            TotalDishForOrder = BD.rs.getString(1) + "\n" + TotalDishForOrder;
            TotalPriceForOrder = BD.rs.getInt(2) + TotalPriceForOrder;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void getPhoneAndAddress(Message message) throws SQLException {
        forKeyWords=true;
        if (TotalDish != null) {
            userPhone = getPhoneNumb(message);
            if (message.getText().equals(userPhone)) {
                sendMsg(message, "Укажите адрес, куда нужно доставить еду.");
                takePhone = message.getText();
                Phone=takePhone;
                String updatePhone="UPDATE orders SET phone = '"+Phone+"' where user_id='"+message.getChatId()+"'";
                try {
                    BD.stmt.executeUpdate(updatePhone);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        if ((takePhone != null) & (message.getText().matches("[0-9]{0,4}[^0-9]{0,2}[а-я].+[0-9]{1,4}[^а-я]{0,1}[А-я]{0,1}"))) {
            context = new GeoApiContext().setApiKey("AIzaSyAg5cKfRFcLIxAUuPSs8IFXX5dnbH844uw");
            address = jsonR.makeURL(message);
            sendMsg(message, address);
            if(address=="Такой улицы нет или сервер не отвечает. Введите адрес заново")
            {
                address = jsonR.makeURL(message);
            }
            else {
                String updateAddress="UPDATE orders SET address='"+address+"' where user_id='"+message.getChatId()+"'";
                try {
                    BD.stmt.executeUpdate(updateAddress);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String caca1="SELECT address,phone FROM orders where user_id='"+message.getChatId()+"'";
                try {
                   BD.rs=BD.stmt.executeQuery(caca1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                while (BD.rs.next()) {
                    address=BD.rs.getString(1);
                    Phone=BD.rs.getString(2);
                }
                sendMsg(message, "Ваш телефон :" + Phone + "\n"
                        + "Ваш адрес :" +address+"\n"
                        + "Если данные указаны верно, введите 'Да'\n"
                        + "В случае ошибки введите 'Нет'");
            }
            forKeyWords=false;
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
        UserIntoBD us = new UserIntoBD();
        SuperKeyWord keyw = new SuperKeyWord();
        Dish td = new Dish();
        jsonR = new JsonR();
        try {
            us.usrintbd(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (message != null && message.hasText() && message.getText().contains("/")) {
            switch (cmd) {
                case "/start":
                    hello(message);
                    sendMsg(message, "Привет," + user_name);
                    sendMsg(message, "FoodBot - чат бот, с помощью которого вы сможете заказывать еду в любое место в городе Ростове-на-Дону. " +
                            "" +
                            "При заказе еды используется ваш номер мобильного телефона, чтобы курьер мог связаться с вами. После того, как вы определитесь с заказом " +
                            ", введете номер мобильного и адрес, куда необходимо доставить заказ, бот автоматически выберет ближайший ресторан. " +
                            "Будьте внимательны при вводе ваших данных. После подтверждения адреса и телефона заказ отменить уже нельзя!" + "\n");
                    sendMsg(message, "Cписок команд:  \n\n" +
                            "/start - начало работы с ботом" +
                            "\n/top5 - выводит топ 5 блюд, которые заказывали пользователи" +
                            "\n/help - выводит список команд" +
                            "\n/clear - очищает заказ" +
                            "\n/favlist - выводит список блюд, которые вы заказывали чаще всего \n\n" +
                            "\t Описание работы с ботом \n\n" +
                            "Для начала заказа введите ключевые слова или названия блюд." +
                            "Ключевые слова это общее название блюда, например: шаурма, суп" +
                            "\nДля того чтобы заверишь заказ введите Стоп.\nЧтобы убрать блюдо из общей корзины введите 'Я не хочу' и название блюда.");
                    break;
                case "/help":
                    sendMsg(message, "Доступные команды:  \n\n" +
                            "/start - начало работы с ботом" +
                            "\n/top5 - выводит топ 5 блюд, которые заказывали пользователи" +
                            "\n/help - выводит список команд" +
                            "\n/clear - очищает заказ" +
                            "\n/favlist - выводит список блюд, которые вы заказывали чаще всего \n\n" +
                            "\t Описание работы с ботом \n\n" +
                            "Для начала заказа следуйте инструкции:\n" +
                            "1 :Введите ключевое слово.Ключевые слова это общее название блюда, например: шаурма, суп\n" +
                            "2 :Чтобы заказать блюдо из списка введите его точное название, в точности как в списке\n" +
                            "3 :Для того, чтобы завершить заказ введите Стоп\n" +
                            "4 :Для связи с вами потребуется Ваш номер мобильного телефона. После завершения заказа, вы не сможете его отменить.\n" +
                            "5 :Почти последним шагом является ввод Вашего адреса. Если вы ошиблись при вводе, вы сможете изменить данные чуть позже.\n" +
                            "6 :И после того, как вы все подтвердили вы увидите примерное время Вашего заказа. Ожидайте звонка курьера.\n" +
                            "Дальнейшее использование чат-бота подтверждает, что Вы согласны на хранение и обработку Вашим персональных данных, если Вы не согласны, пожалуйста выйдите!");
                    break;
                case "/favlist":
                    String favlistQuery = "SELECT dish_name,COUNT(id_dish) FROM `log` WHERE user_id='" + user_id + "' GROUP BY id_dish ORDER BY COUNT(*) DESC LIMIT 4";
                    try {
                        int number = 1;
                        char end = ' ';
                        BD.rs = BD.stmt.executeQuery(favlistQuery);
                        sendMsg(message, "Ваши наиболее заказываемые блюда\n");
                        if (BD.rs.first() == false) {
                            sendMsg(message, "Вы ничего не  заказывали");
                        } else {
                            BD.rs.beforeFirst();

                            while (BD.rs.next()) {
                                for (i = 5; i < 21; i++) {
                                    if ((BD.rs.getInt(2) == i) && (String.valueOf(BD.rs.getInt(2)).endsWith("1")) && (String.valueOf(BD.rs.getInt(2)).endsWith("6")) && (String.valueOf(BD.rs.getInt(2)).endsWith("7")) && (String.valueOf(BD.rs.getInt(2)).endsWith("8")) && (String.valueOf(BD.rs.getInt(2)).endsWith("9"))) {
                                        end = ' ';
                                    }
                                }
                                if (String.valueOf(BD.rs.getInt(2)).endsWith("2") || (String.valueOf(BD.rs.getInt(2)).endsWith("3") || (String.valueOf(BD.rs.getInt(2)).endsWith("4")))) {
                                    end = 'a';
                                }
                                sendMsg(message, number + " " + BD.rs.getString(1) + " закали  " + BD.rs.getInt(2) + " раз" + end + "\n");
                                number++;
                            }
                        }
                    } catch (SQLException e) {

                        e.printStackTrace();
                    }
                    break;
                case "/top5":
                    user_id = jsonR.takeUserIdFromMessage(message);
                    String top5Query = "SELECT dish FROM `log` GROUP BY id_dish ORDER BY COUNT(*) DESC LIMIT 5";
                    try {
                        int number = 1;
                        BD.rs = BD.stmt.executeQuery(top5Query);
                        sendMsg(message, "TОП 5 блюд, которые заказывают пользователи\n");
                        while (BD.rs.next()) {
                            sendMsg(message, number + " " + BD.rs.getString(1) + "\n");
                            number++;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/clear":
                    String DeleteAllFromOrder = "DELETE  FROM `orders` WHERE user_id='" + message.getChatId() + "'";
                    try {
                        BD.stmt.executeUpdate(DeleteAllFromOrder);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    sendMsg(message,"Ваш заказ очищен");
                default:
                    sendMsg(message, "Нет такой команды. Для полного списка команд используйте /help");
            }
        }
        if (message.getText().equalsIgnoreCase("привет")) {
            sendMsg(message, "Привет, проголодался?");
        }
        try {
            DishName = td.findDish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            String ChecktoErrorQuery = "SELECT dish_name FROM `dish` WHERE dish_name='" + message.getText() + "'";
            BD.rs = BD.stmt.executeQuery(ChecktoErrorQuery);
            if (BD.rs.next()) {
                letmeError = true;
            } else {
                letmeError = false;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (message.getText().contains("Я не хочу ") || (message.getText().contains("я не хочу "))) {
            takeUserIdFromDB(message);


            if(message.getChatId()== userIdFromDB) {
                if (Price == 0) {
                    sendMsg(message, "Вы ничего не заказали");
                } else {
                   takeOrderForUser(message);
                    for (i = 0; i < DishName.length; i++) {


                        if ((TotalDishForOrder.contains(DishName[i])) && (message.getText().equalsIgnoreCase("Я не хочу " + DishName[i]))) {//////////////////// тотал строка с заказом( брать из базы)
                            String msgText = message.getText().toLowerCase().replace("я не хочу ", "");

                            TotalDishForOrder="";
                            TotalPriceForOrder=0;
                            try {
                                String DeleteFromOrder = "DELETE  FROM `orders` WHERE dish_name='" + msgText + "' and user_id='" + message.getChatId() + "' LIMIT 1";
                                BD.stmt.executeUpdate(DeleteFromOrder);
                                takeOrderForUser(message);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    sendMsg(message, "Ваш заказ : " + "\n" + TotalDishForOrder + " " + "\n" + "на сумму :" + TotalPriceForOrder + " rub");
                    TotalDishForRest=TotalDishForOrder;
                    TotalPriceForRest=TotalPriceForOrder;
                    TotalDishForOrder = "";
                    TotalPriceForOrder = 0;
                }
            }
        } else {
            for (i = 0; i < DishName.length; i++) {
                if ((message.getText().equalsIgnoreCase(DishName[i]))) {
                    String sdf = "SELECT * FROM `Dish` WHERE dish_name ='" + DishName[i] + "'";
                    try {
                        BD.rs = BD.stmt.executeQuery(sdf);
                        while (BD.rs.next()) {
                            if (BD.rs.getInt(7) == 0) {
                                sendMsg(message, "Сейчас это блюдо не подается");
                                forKeyWords = true;
                            } else {
                                DishQuery = "SELECT * FROM `Dish` WHERE dish_name ='" + DishName[i] + "'";
                                try {
                                    BD.rs = BD.stmt.executeQuery(DishQuery);
                                    while (BD.rs.next()) {
                                        sendMsg(message, "\n" + "\n" + "\n" + "Название : " + BD.rs.getString(2) + "\n" + "Описание : "
                                                + BD.rs.getString(4) + "\n" + "Цена :" + BD.rs.getInt(5) + " rub " + "\n" + "Ингридиенты  :" + BD.rs.getString(6) + "\n" + "Фото : " + BD.rs.getString(3));
                                        Dish = BD.rs.getString(2);
                                        Price = BD.rs.getInt(5);
                                        TotalDishForLog = "dish=" + BD.rs.getString(2) + "price=" + BD.rs.getInt(5) + "id=" + BD.rs.getString(1) + "\n" + TotalDishForLog;
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    String UpdateOrder = "INSERT INTO orders SET" +
                                            " dish_name = '" + Dish + "'," +
                                            "price ='" + Price + "', user_id ='" + user_id + "'," +
                                            "address = 'address'," + "phone = 0";
                                    BD.stmt.executeUpdate(UpdateOrder);

                                } catch (SQLException sqlEx) {
                                    sqlEx.printStackTrace();
                                }
                                takeOrderForUser(message);
                                sendMsg(message, "Итоговая стоимость = " + TotalPriceForOrder + " rub");
                                sendMsg(message, "Итоговый заказ : " + "\n" + TotalDishForOrder);//приколы с пустыми строками
                                TotalDishForRest = TotalDishForOrder;
                                TotalPriceForRest = TotalPriceForOrder;
                                TotalDishForOrder = " ";
                                TotalPriceForOrder = 0;
                                forKeyWords = true;
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
            }
            if (!forKeyWords) {
                try {
                    DishesonKW = keyw.findDishKW(message);
                    if (DishesonKW != null) {
                        for (i = 0; i < DishesonKW.length; i++) {
                            DishQuery = "SELECT * FROM `dish` WHERE dish_name='" + DishesonKW[i] + "'";
                            sendMsg(message, (i + 1 + ")"));
                            BD.rs = BD.stmt.executeQuery(DishQuery);
                            while (BD.rs.next()) {
                                sendMsg(message, "\n" + "\n" + "\n" + "Название : " + BD.rs.getString(2) + "\n" + "Описание : " + BD.rs.getString(4) + "\n" +
                                        "Цена :" + BD.rs.getInt(5) + " rub " + "\n" + "Ингридиенты  :" + BD.rs.getString(6) + "\n" + "Фото : " + BD.rs.getString(3));
                            }
                        }
                        sendMsg(message, "Введите название блюда");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            forKeyWords = false;
            if (message.getText().equalsIgnoreCase("стоп")) {
               takeUserIdFromDB(message);


                if(message.getChatId()== userIdFromDB) {
                    if (Dish != null && Price != 0) {
                        takeOrderForUser(message);
                        sendMsg(message, "\tВаш заказ :\n" + TotalDishForOrder + " " + "\nна сумму :" + TotalPriceForOrder + " rub");
                        TotalDishForOrder = " ";
                        TotalPriceForOrder = 0;
                        sendMsg(message, "Введите номер мобильного телефона ( по формату 89ХХХХХХХХХ), чтобы курьер смог связаться с вами");
                    } else {
                        sendMsg(message, "Закажите что-нибудь.Надо поесть");
                    }
                }
                else
                {sendMsg(message,"Закажите что-нибудь.Надо поесть");}

            }
            userIdFromDB=0;
            takeUserIdFromDB(message);
            if(message.getChatId()== userIdFromDB) {
                try {
                    getPhoneAndAddress(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

           takeUserIdFromDB(message);


            if(message.getChatId()== userIdFromDB) {
                if ((takePhone != null) && (address != null)) {
                    String gol = message.getText().toLowerCase();
                    switch (gol) {
                        case "да":
                            String DeleteAllFromOrder = "DELETE  FROM `orders` WHERE user_id='" + message.getChatId() + "'";
                        try {
                            BD.stmt.executeUpdate(DeleteAllFromOrder);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                            takePhone = null;
                            user_id = jsonR.takeUserIdFromMessage(message);
                            DateForLog = message.getDate();
                            String LastName = "SELECT last_name FROM user WHERE id='" + user_id + "'";
                            try {
                                BD.rs = BD.stmt.executeQuery(LastName);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            try {
                                while (BD.rs.next()) {
                                    user_secname = BD.rs.getString(1);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            try {
                                String closrest = jsonR.chooseClosRest(address);
                                sendMsg(message, "Номер Вашего заказа :" + OrderNumber + "\nОжидайте Ваш заказ.");
                                sendMsg(message, closrest);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            try {
                                userIdRest = jsonR.takeUserIdRest();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            hello(message);

                            String ta = TotalDishForLog.replace("\n", "-");
                            String[] boom = ta.split("-");
                            sendMsgToRest(message, "Номер заказа: " + OrderNumber +
                                    "\nАдрес клиента: " + address +
                                    "\nТелефон клиента: " + Phone +
                                    "\nЗаказ:\n" + TotalDishForRest +
                                    "\n\nИтоговая стоимость: " + TotalPriceForRest + " руб");
                            OrderNumber++;
                            for (int k = 0; k < boom.length - 1; k++) {
                                DishForLog = boom[k].replaceAll("dish=", "").replaceAll("price=+[0-9]+.*", "");
                                PriceForLog = Integer.valueOf(boom[k].replaceAll("[^0-9]+price=", "").replaceAll("id=+.+", ""));
                                IdDishForLog = Integer.valueOf(boom[k].replaceAll("[\\S\\s]+id=", ""));
                                TotalDishForRest = "";
                                address = null;
                                letmeError = true;
                                try {
                                    UserIdRestForLog = jsonR.takeIdRest();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    String logvalues = "INSERT INTO log " +
                                            "SET" +
                                            " user_id = '" + user_id + "'," +
                                            " user_name = '" + user_name + "'," +
                                            " user_secname = '" + user_secname + "', " +
                                            " `dish` = '" + DishForLog + "'," +
                                            " `date_msg` = '" + String.valueOf(DateForLog) + "'," +
                                            " `price` =  '" + PriceForLog + "', " +
                                            " `id_res` = '" + UserIdRestForLog + "'," +
                                            " `id_dish` = '" + IdDishForLog + "' ";
                                    BD.stmt.executeUpdate(logvalues);
                                } catch (SQLException sqlEx) {
                                    sqlEx.printStackTrace();
                                }
                            }
                            break;
                        case "нет":
                            sendMsg(message, "\nВведите данные повторно");
                            sendMsg(message, "Введите номер мобильного телефона ( по формату 89ХХХХХХХХХ), чтобы курьер смог связаться с вами");
                            takeUserIdFromDB(message);
                            if(message.getChatId()== userIdFromDB) {
                                try {
                                    getPhoneAndAddress(message);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                }
            }
            if (message.getText().matches("timeismoney+.*[А-я]+.[0-9]{0,4}")) {
                Boolean bool = true;
                Long IdUser = (message.getChatId());
                String Address = message.getText().substring(12).trim();
                String UserFromRest = "SELECT DISTINCT resbuild.address FROM `user`,`res`,`resbuild`";
                try {
                    BD.rs = BD.stmt.executeQuery(UserFromRest);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    while (BD.rs.next()) {
                        if (Address.equals(BD.rs.getString(1))) {
                            String AddIdUserQuery = "UPDATE resbuild SET resbuild.user_id='" + IdUser + "' WHERE resbuild.address='" + Address + "'";
                            BD.stmt.executeUpdate(AddIdUserQuery);
                            bool = true;
                        } else {
                            bool = false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (!bool) {
                    sendMsg(message, "Введите секретное слово и адрес Вашего ресторана");
                } else {
                    sendMsg(message, "К Вашему ресторану привязан аккаунт telegram");
                }
                letmeError = true;
            }
            if ((takePhone == null) && (address == null) && (DishesonKW == null) && (message.getText().equalsIgnoreCase("стоп") == false)
                    && (message.getText().equalsIgnoreCase("Привет") == false) && (message.getText().contains("Я не хочу") == false)
                    && (message.getText().contains("/") == false) && (letmeError == false) && (message.getText().contains("я не хочу") == false)) {
                sendMsg(message, "Извините, я вас не понимаю.Для заказа введите ключевое слово или название блюда. Для помощи введите /help");
            }
        }
    }
}








