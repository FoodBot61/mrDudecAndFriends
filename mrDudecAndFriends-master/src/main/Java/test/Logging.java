package test;
import java.sql.SQLException;
import java.util.regex.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import java.io.FileWriter;
import java.io.*;
import java.io.File;

/**
 * Created by user on 15.01.2017.
 */
public class Logging extends SimpleBot {

    public static void main(String[] args) throws IOException {

    }
    public void log(Message message) {

        BD bd = new BD();
        bd.BDsher();
        try {
            String fs=message.getText().toString();

            String sql = "INSERT INTO log " +
                    "VALUES ('7'," +
                    " 1," +
                    " 'Sasha'," +
                    " 'Kulik', " +
                    "'"+fs+"'," +
                    " '1990-03-10'," +
                    " 100, " +
                    "1," +
                    " 1," +
                    " 'Puskina'," +
                    " 1 )";
            BD.stmt.executeUpdate(sql);
if(BD.stmt!=null){
    System.out.println("eeeeee rock"+fs);

}
else {

    System.out.println("=-(");

}
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}
