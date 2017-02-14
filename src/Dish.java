import org.telegram.telegrambots.api.objects.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27.01.2017.
 */
public class Dish {
    String [] ListofDishh;
    List rowValues = new ArrayList();
    String DishQuery;
    public String [] findDish() throws SQLException {
    DishQuery= "SELECT dish_name FROM `dish` WHERE 1";
    BD.rs = BD.stmt.executeQuery(DishQuery);
    while (BD.rs.next()) {
        rowValues.add(BD.rs.getString(1));
    }
    ListofDishh= (String[]) rowValues.toArray(new String[rowValues.size()]);
    return ListofDishh;
    }
}
