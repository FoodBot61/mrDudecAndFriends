package sd;

import org.telegram.telegrambots.api.objects.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27.01.2017.
 */
public class Dish {
    String [] test_dishh;
    String ccc;
    List rowValues1 = new ArrayList();
    int i;
public String [] TestDish(Message message) throws SQLException {


    String sql1 = "SELECT dish_name FROM `dish` WHERE 1";
    BD.rs = BD.stmt.executeQuery(sql1);
    while (BD.rs.next()) {
        rowValues1.add(BD.rs.getString(1));
    }
    test_dishh = (String[]) rowValues1.toArray(new String[rowValues1.size()]);

    return test_dishh;
}
}
