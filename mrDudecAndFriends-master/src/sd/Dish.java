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
    List rowValues = new ArrayList();
    String ListofDish;

public String [] TestDish() throws SQLException {
    ListofDish = "SELECT dish_name FROM `dish` WHERE 1";
    BD.rs = BD.stmt.executeQuery(ListofDish);
    while (BD.rs.next()) {
        rowValues.add(BD.rs.getString(1));
    }
    test_dishh = (String[]) rowValues.toArray(new String[rowValues.size()]);
    return test_dishh;
    }
}
