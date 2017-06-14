import com.google.maps.GeoApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 08.01.2017.
 */


public class SimpleBot extends TelegramLongPollingBot {
    int OrderNumber = 1;
    String[] RestIds;
    int IdRest;
    List ListOfIds = new ArrayList();
    long UserFromRestID;
    String LoginsQuery;
    String[] logins;
    String closrest;
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
    private JsonR jsonR;
    private String user_name;
    private String user_secname;
    private String user_id;
    private int DateForLog;
    private int PriceForLog;
    private int IdDishForLog;
    private String UserIdRestForLog;
    private String DishForLog;
    private String TotalDishForRest = " ";
    private int TotalPriceForRest;
    private Boolean letmeError;
    private String TotalDishForLog= " ";
    private int TotalPriceForOrder;
    private String TotalDishForOrder = " ";
    private int userIdFromDB;
    private List ListOfLogins = new ArrayList();
    private int DishId;
    private boolean takeaddress=true;
    HashMap <String,String> MaxTimeToCook = new HashMap<>();
    private boolean pressstop = false;
    private boolean PhoneAway = false;
    private boolean AddressAway= false;
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
            Pattern p = Pattern.compile("89.[0-9]{8}$");
            Matcher m = p.matcher(msgText);
            if (m.find()) {
                phone = msgText.substring(m.end() - 11, m.end());
        }
            return phone;

    }

    private void sendMsgToRest(Message message, String text, long userIdRest) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(userIdRest);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void takeUserIdFromDB(Message message) {      //получение id пользователя по id чата телеги
        String takeUserIdQuery = "SELECT user_id FROM orders WHERE user_id='" + message.getChatId() + "'";
        try {
            BD.rs = BD.stmt.executeQuery(takeUserIdQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (BD.rs.next()) {
                userIdFromDB = BD.rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void takeOrderForUser(Message message) { //блюда из общего заказа
        try {
            TotalDishQuery = "SELECT dish_name,price FROM orders WHERE user_id='" + message.getChatId() + "'";
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
        if ((TotalDishForOrder!= null) && (pressstop)) {
            letmeError = true;
            userPhone = getPhoneNumb(message);
            if ((userPhone == null) && (!PhoneAway)) {
                sendMsg(message, "Введите номер мобильного телефона ( по формату 89ХХХХХХХХХ), чтобы курьер смог связаться с вами");
            } else {
                if (message.getText().equals(userPhone)) {
                    PhoneAway = true;
                    takeaddress = false;
                    takePhone = message.getText();
                    Phone = takePhone;
                    String updatePhone = "UPDATE orders SET phone = '" + Phone + "' where user_id='" + message.getChatId() + "'";
                    try {
                        BD.stmt.executeUpdate(updatePhone);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if ((Phone != null) &  (pressstop)& (message.getText().matches("[0-9]{0,4}[^0-9]{0,2}[а-я].+[0-9]{1,4}[^а-я]{0,1}[А-я]{0,1}"))) {
            context = new GeoApiContext().setApiKey("AIzaSyAg5cKfRFcLIxAUuPSs8IFXX5dnbH844uw");
            address = jsonR.makeURL(message);
            sendMsg(message, address);
            if (address == "Такой улицы нет или сервер не отвечает. Введите адрес заново") {
                address = jsonR.makeURL(message);
                forKeyWords=false;
            } else {
                String updateAddress = "UPDATE orders SET address='" + address + "' where user_id='" + message.getChatId() + "'";
                try {
                    BD.stmt.executeUpdate(updateAddress);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                AddressAway = true;
                String caca1 = "SELECT address,phone FROM orders where user_id='" + message.getChatId() + "'";
                try {
                    BD.rs = BD.stmt.executeQuery(caca1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                while (BD.rs.next()) {
                    address = BD.rs.getString(1);
                    Phone = BD.rs.getString(2);
                }
                sendMsg(message, "Ваш телефон :" + Phone + "\n"
                        + "Ваш адрес :" + address + "\n"
                        + "Если данные указаны верно, введите 'Да'\n"
                        + "В случае ошибки введите 'Нет'");
            }
            forKeyWords = false;
            takeaddress =true;
        }
        else
        {
            if((Phone!=null)&&(!AddressAway)&&(pressstop)) {
                sendMsg(message, "Укажите адрес, куда нужно доставить еду. Например Большая Садовая 91");
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
                            "\t Описание работы с ботом представлено в команде /help\n\n");
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
                            "2 :Чтобы заказать блюдо из списка введите '\nНазвание блюда и з Название ресторана'\n.Названия блюд и ресторано ввводите в точности как в списке\n" +
                            "Например: Суп и з Ресторан\n" +
                            "3 :Если необходимо вычеркнуть блюдо из общего заказа, введите Я не хочу Название Блюда и з Название ресторана\n" +
                            "Например: Я не хочу Суп и з Ресторан\n" +
                            "4 :Для того, чтобы завершить заказ введите Стоп\n" +
                            "5 :Для связи с вами потребуется Ваш номер мобильного телефона. После завершения заказа, вы не сможете его отменить.\n" +
                            "6 :Почти последним шагом является ввод Вашего адреса. Если вы ошиблись при вводе, вы сможете изменить данные чуть позже.\n" +
                            "7 :И после того, как вы все подтвердили вы увидите примерное время Вашего заказа. Ожидайте звонка курьера.\n" +
                            "Дальнейшее использование чат-бота подтверждает, что Вы согласны на хранение и обработку Вашим персональных данных, если Вы не согласны, пожалуйста выйдите!\n" +
                            "Ознакомиться с пользовательским соглашением Вы можете здесь : " +
                            "\nОзнакомиться с политикой в отношении обработки персональных данных Вы можете здесь : ");
                    break;
                case "/favlist":
                    String favlistQuery = "SELECT dish,COUNT(id_dish) FROM `log` WHERE user_id='" + user_id + "' GROUP BY id_dish ORDER BY COUNT(*) DESC LIMIT 4";
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
                    sendMsg(message, "Ваш заказ очищен");
                    break;
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
        letmeError = false;
        if (message.getText().contains("Я не хочу ") || (message.getText().contains("я не хочу ")) || (message.getText().contains("Я НЕ ХОЧУ "))) {
            letmeError = true;
            takeUserIdFromDB(message);
            if (message.getChatId() == userIdFromDB) {
                if (Price == 0) {
                    sendMsg(message, "Вы ничего не заказали");
                } else {
                    takeOrderForUser(message);
                    for (i = 0; i < DishName.length; i++) {
                        if ((TotalDishForOrder.contains(DishName[i])) && (message.getText().equalsIgnoreCase("Я не хочу " + DishName[i]))) {//////////////////// тотал строка с заказом( брать из базы)
                            TotalDishForOrder = "";
                            TotalPriceForOrder = 0;
                            try {
                                String DeleteFromOrder = "DELETE  FROM `orders` WHERE dish_name='" + DishName[i] + "' and user_id='" + message.getChatId() + "' LIMIT 1";
                                BD.stmt.executeUpdate(DeleteFromOrder);
                                takeOrderForUser(message);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    sendMsg(message, "Ваш заказ : " + "\n" + TotalDishForOrder + " " + "\n" + "на сумму :" + TotalPriceForOrder + " rub");
                    TotalDishForOrder = "";
                    TotalPriceForOrder = 0;
                }
            }
        } else {
            for (i = 0; i < DishName.length; i++) {
                    if ((message.getText().equalsIgnoreCase(DishName[i]))) {
                        letmeError = true;
                        String[] dish = DishName[i].split(" и з ");
                        String aboutdish = "SELECT * FROM `Dish` WHERE dish_name ='" + dish[0] + "'";
                        try {
                            BD.rs = BD.stmt.executeQuery(aboutdish);
                            while (BD.rs.next()) {
                                DishQuery = "SELECT dish.id,dish.dish_name,dish.icons,dish.descr_dish,dish.price,dish.ingredient,res.name,res.id,dishes.acs " +
                                        "FROM `dish`,`res`,`dishes` where dish_name='" + dish[0].trim() + "'" +
                                        " and dishes.id_dish=dish.id and res.id=dish.id_res and res.name='" + dish[1].trim() + "'";
                                try {
                                    BD.rs = BD.stmt.executeQuery(DishQuery);
                                    while (BD.rs.next()) {
                                        if (BD.rs.getInt(9) == 0) {
                                            sendMsg(message, "Это блюдо сейчас не подается");
                                            forKeyWords = true;
                                        } else {
                                            sendMsg(message, "\n" + "\n" + "\n" + "Название : " + BD.rs.getString(2) + "\n"
                                                    + "Ресторан : " + BD.rs.getString(7) + "\n" + "Описание : "
                                                    + BD.rs.getString(4) + "\n" + "Цена :" + BD.rs.getInt(5) + " rub " + "\n" + "Ингридиенты  :" + BD.rs.getString(6) + "\n" + "Фото : " + BD.rs.getString(3));
                                            Dish = DishName[i];
                                            Price = BD.rs.getInt(5);
                                            IdRest = BD.rs.getInt(8);
                                            DishId = BD.rs.getInt(1);
                                            TotalDishForLog = "dish=" + BD.rs.getString(2) + "price=" + BD.rs.getInt(5) + "id=" + BD.rs.getString(1) + "\n" + TotalDishForLog;
                                            try {
                                                    String UpdateOrder = "INSERT INTO orders SET" +
                                                            " dish_name = '" + Dish + "'," +
                                                            "price ='" + Price + "', user_id ='" + user_id + "'," +
                                                            "id_res = '" + IdRest + "'," +
                                                            "id_dish = '" + DishId + "'," +
                                                            "address = 'address'," + "phone = 0";
                                                    BD.stmt.executeUpdate(UpdateOrder);

                                                takeOrderForUser(message);
                                                sendMsg(message, "Итоговая стоимость = " + TotalPriceForOrder + " rub");
                                                sendMsg(message, "Итоговый заказ : " + "\n" + TotalDishForOrder);//приколы с пустыми строками
                                                TotalDishForOrder = " ";
                                                TotalPriceForOrder = 0;
                                                forKeyWords = true;
                                            } catch (SQLException sqlEx) {
                                                sqlEx.printStackTrace();
                                            }
                                        }
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            if (!forKeyWords && takeaddress) {
                try {
                    DishesonKW = keyw.findDishKW(message);
                    if (DishesonKW != null) {
                        for (i = 0; i < DishesonKW.length; i++) {
                            String[] dish = DishesonKW[i].split(" и з ");
                            DishQuery = "SELECT dish.id,dish.dish_name,dish.icons,dish.descr_dish,dish.price,dish.ingredient,res.name FROM `dish`,`res`,`dishes`,`key_words` " +
                                    "where dishes.id_dish=dish.id and res.id=dish.id_res and key_words.id=dishes.id_keyword " +
                                    "and dish.dish_name='" + dish[0].trim() + "' and res.name='" + dish[1].trim() + "'";
                            sendMsg(message, (i + 1 + ")"));
                            BD.rs = BD.stmt.executeQuery(DishQuery);
                            while (BD.rs.next()) {
                                sendMsg(message, "\n" + "\n" + "\n" + "Название : " + BD.rs.getString(2) + "\n"
                                        + "Ресторан : " + BD.rs.getString(7) + "\n" + "Описание : " + BD.rs.getString(4) + "\n" +
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
                pressstop=true;
                takeUserIdFromDB(message);
                if (message.getChatId() == userIdFromDB) {
                    if (Dish != null && Price != 0) {
                        takeOrderForUser(message);
                        sendMsg(message, "\tВаш заказ :\n" + TotalDishForOrder + " " + "\nна сумму :" + TotalPriceForOrder + " rub");
                        TotalDishForOrder = " ";
                        TotalPriceForOrder = 0;
                    } else {
                        sendMsg(message, "Закажите что-нибудь.Надо поесть");
                    }
                } else {
                    sendMsg(message, "Закажите что-нибудь.Надо поесть");
                }
                String MaxTimetoCookQuery = "SELECT DISTINCT orders.id_res, MAX(dish.timetocook) FROM orders,dish" +
                        " WHERE user_id='" + message.getChatId() + "' and dish.id=orders.id_dish GROUP BY dish.id_res";
                try {
                    BD.rs = BD.stmt.executeQuery(MaxTimetoCookQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    while (BD.rs.next()) {
                        MaxTimeToCook.put(BD.rs.getString(1),BD.rs.getString(2));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            userIdFromDB = 0;
            takeUserIdFromDB(message);
            if (message.getChatId() == userIdFromDB) {
                try {

                    forKeyWords = true;
                    getPhoneAndAddress(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            takeUserIdFromDB(message);
            if (message.getChatId() == userIdFromDB) {
                if ((takePhone != null) && (address != null)) {
                    String gol = message.getText().toLowerCase();
                    switch (gol) {
                        case "да":
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
                            hello(message);
                            String ta = TotalDishForLog.replace("\n", "-");
                            String[] boom = ta.split("-");
                            sendMsg(message, "Номер Вашего заказа :" + OrderNumber + "\nОжидайте Ваш заказ.");
                            String idRestQuery = "SELECT DISTINCT id_res FROM `orders` WHERE user_id='" + message.getChatId() + "'";
                            try {
                                BD.rs = BD.stmt.executeQuery(idRestQuery);
                                while (BD.rs.next()) {
                                    ListOfIds.add(BD.rs.getString(1));
                                }
                                RestIds = (String[]) ListOfIds.toArray(new String[ListOfIds.size()]);
                                for (i = 0; i < RestIds.length; i++) {
                                    String OrderQuery = "SELECT DISTINCT orders.address,orders.dish_name, orders.price,orders.phone,resbuild.user_id " +
                                            "FROM `orders`,`resbuild` WHERE orders.user_id='" + message.getChatId() + "' and resbuild.id_res=orders.id_res and orders.id_res='" + RestIds[i] + "'";
                                    try {
                                        BD.rs = BD.stmt.executeQuery(OrderQuery);
                                        while (BD.rs.next()) {
                                            TotalDishForRest = BD.rs.getString(2) + "\n" + TotalDishForRest;
                                            TotalPriceForRest = BD.rs.getInt(3) + TotalPriceForRest;
                                            UserFromRestID = BD.rs.getLong(5);
                                        }
                                        sendMsgToRest(message, "Номер заказа: " + OrderNumber +
                                                "\nАдрес клиента: " + address +
                                                "\nТелефон клиента: " + Phone +
                                                "\nЗаказ:\n" + TotalDishForRest +
                                                "\n\nИтоговая стоимость: " + TotalPriceForRest + " руб", UserFromRestID);
                                        TotalDishForRest = " ";
                                        TotalPriceForRest = 0;
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        closrest = jsonR.chooseClosRest(address,RestIds[i], Double.parseDouble(MaxTimeToCook.get(RestIds[i])));
                                        sendMsg(message, closrest);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            ListOfIds.clear();
                            OrderNumber++;
                            String DeleteAllFromOrder = "DELETE  FROM `orders` WHERE user_id='" + message.getChatId() + "'";
                            try {
                                BD.stmt.executeUpdate(DeleteAllFromOrder);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
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
                            Phone=null;
                            address=null;
                            pressstop = false;
                            PhoneAway = false;
                            AddressAway= false;
                            break;
                        case "нет":
                            Phone=null;
                            address=null;
                            pressstop = true;
                            PhoneAway = false;
                            AddressAway= false;
                            sendMsg(message, "\nВведите данные повторно");
                            //sendMsg(message, "Введите номер мобильного телефона ( по формату 89ХХХХХХХХХ), чтобы курьер смог связаться с вами");

                            takeUserIdFromDB(message);
                            if (message.getChatId() == userIdFromDB) {
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
            LoginsQuery = "SELECT login FROM res";
            try {
                BD.rs = BD.stmt.executeQuery(LoginsQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                while (BD.rs.next()) {
                    ListOfLogins.add(BD.rs.getString(1));
                }
                logins = (String[]) ListOfLogins.toArray(new String[ListOfLogins.size()]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (i = 0; i < ListOfLogins.size(); i++) {
                if (message.getText().equals("Time is money " + logins[i])) {
                    Boolean bool = true;
                    Long IdUser = (message.getChatId());
                    String UserFromRest = "SELECT DISTINCT res.login FROM `res`";
                    try {
                        BD.rs = BD.stmt.executeQuery(UserFromRest);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        String AddIdUserQuery = "UPDATE resbuild,res SET resbuild.user_id='" + IdUser + "' WHERE resbuild.id_res=res.id " +
                                "and res.login='" + logins[i] + "'";
                        BD.stmt.executeUpdate(AddIdUserQuery);
                        bool = true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (!bool) {
                        sendMsg(message, "Введите секретное слово и логин Вашего ресторана");
                    } else {
                        sendMsg(message, "К Вашему ресторану привязан аккаунт telegram");
                    }
                    letmeError = true;
                }
            }
            forKeyWords = false;
            if ((takePhone == null) && (address == null) && (DishesonKW == null) && (message.getText().equalsIgnoreCase("стоп") == false)
                    && (message.getText().equalsIgnoreCase("Привет") == false) && (message.getText().contains("Я не хочу") == false)
                    && (message.getText().contains("/") == false) && (letmeError == false) && (message.getText().contains("я не хочу") == false)) {
                sendMsg(message, "Извините, я вас не понимаю.Для заказа введите ключевое слово или Название блюда и з Название ресторана.\nДля помощи введите /help");
            }
        }
    }
}








