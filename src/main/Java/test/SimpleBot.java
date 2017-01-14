package test;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import test.BD;
import static java.lang.System.exit;

/**
 * Created by User on 08.01.2017.
 */

public class SimpleBot extends TelegramLongPollingBot {
BD rs;

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

                if (message != null && message.hasText()) {

                    switch (cmd){
                        case "привет":sendMsg(message, "Привет, проголодался?");break;
                        case "/help": sendMsg(message,"top5 - выводит топ 10000000 блюд \n help - выводит список команд \n favlist - бла бла бла ");break;
                        case "/top5": sendMsg(message, "gjkndgnuidfvdsvsdvsdvsdvsdvsd");break;
                        case "/favlist":sendMsg(message, "gejgiejig");break;




                        default:sendMsg(message, "Я не знаю что ответить на это");
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


    }



