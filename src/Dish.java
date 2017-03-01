import org.telegram.telegrambots.api.objects.Message;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27.01.2017.
 */
public class Dish {
    private String[] ListofDishh;
    private List rowValues = new ArrayList();
    private String DishQuery;
    private String allDish;
    String allDishQuery;
    public String[] findDish() throws SQLException {
        DishQuery = "SELECT dish_name FROM `dish`";
        BD.rs = BD.stmt.executeQuery(DishQuery);

        while (BD.rs.next()) {

            rowValues.add(BD.rs.getString(1));
        }
        ListofDishh = (String[]) rowValues.toArray(new String[rowValues.size()]);
        return ListofDishh;
    }
}
