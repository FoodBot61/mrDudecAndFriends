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

    public String[] findDish() throws SQLException {
        DishQuery = "SELECT DISTINCT dish.dish_name,res.name FROM `dish`,`res`,`dishes` WHERE res.id=dishes.id_res and dish.id=dishes.id_dish";
        BD.rs = BD.stmt.executeQuery(DishQuery);
        while (BD.rs.next()) {
            String dish=BD.rs.getString(1)+" из "+BD.rs.getString(2);
            rowValues.add(dish);
        }
        ListofDishh = (String[]) rowValues.toArray(new String[rowValues.size()]);
        return ListofDishh;
    }
}
