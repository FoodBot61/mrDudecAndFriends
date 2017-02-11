
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Interner;
import com.google.common.collect.Iterables;

import com.google.maps.*;
import com.google.maps.model.*;
import com.squareup.okhttp.Address;
import jersey.repackaged.com.google.common.collect.Maps;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.Message;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by user on 02.02.2017.
 */
public class JsonR {
    String address;
    String RestAddress;
    double MinDistance;
    final Map<String, String> params = Maps.newHashMap();
    HashMap<String, Double> Distance = new HashMap<>();
    Set<Map.Entry<String, Double>> entrySet = Distance.entrySet();
    String ClosestRest;

    //test var
    String UserId;
    String useridformbd;
    private static String ReadAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    protected static JSONObject read(final String url) throws IOException, JSONException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = ReadAll(rd);
            final JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    protected static String encode(final Map<String, String> params) {
        final String paramsUrl = Joiner.on('&').join(// получаем значение вида key1=value1&key2=value2...
            Iterables.transform(params.entrySet(), new Function<Map.Entry<String, String>, String>() {
               @Override
               public String apply(final Map.Entry<String, String> input) {
                  try {
                      final StringBuffer buffer = new StringBuffer();
                      buffer.append(input.getKey());// получаем значение вида key=value
                      buffer.append('=');
                      buffer.append(URLEncoder.encode(input.getValue(), "utf-8"));// кодируем строку в соответствии со стандартом HTML 4.01
                      return buffer.toString();
                   } catch (final UnsupportedEncodingException e) {
                      throw new RuntimeException(e);
                   }
               }
                }));
        return paramsUrl;
    }

    public String URLmaker(Message message)
    {
        params.put("address", "Ростовская область, Ростов-на-Дону " + message.getText());
        params.put("language", "ru");
        String baseURL = "http://maps.googleapis.com/maps/api/geocode/json";
        final String url = baseURL + '?' + encode(params);
        try {
            final JSONObject response = JsonR.read(url);
            JSONObject location = response.getJSONArray("results").getJSONObject(0);
            address = location.getString("formatted_address");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public String DistanseBe(String address) throws IOException, SQLException {
        String addressQuery = "SELECT address FROM resbuild";
        try {
            BD.rs = BD.stmt.executeQuery(addressQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while (BD.rs.next()) {
            RestAddress = BD.rs.getString(1).replace(" ", "_").replace(",", "_");
            String url = new String("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + address.replace(" ","_").replace(",", "_") + "&destinations=" + RestAddress) + encode(params);
            final JSONObject response = JsonR.read(url);
            String timebetween2loc = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text").replace("mins", "").replace("min", "").trim();
            double TimetoRest = Double.parseDouble(timebetween2loc);
            Distance.put(RestAddress, TimetoRest);
        }
        Object[] DistanceValues = Distance.values().toArray();
        MinDistance = (double) Distance.values().toArray()[0];
        for (int i = 0; i < DistanceValues.length; i++) {
            if (MinDistance > (double) DistanceValues[i]) {
                MinDistance = (double) DistanceValues[i];
            }
        }
        Object KeytoRest= MinDistance;
        for (Map.Entry<String, Double> pair : entrySet) {
            if (KeytoRest.equals(pair.getValue())) {
                ClosestRest = pair.toString();


            }
        }

        UserId=ClosestRest.replaceAll("=.[0-9]+\\.+[0-9]","").replaceFirst("_"," ").replace(".__","., ");
        ClosestRest ="Ближайщий ресторан РИС к вам находится на "+ClosestRest.replace("_"," ").replace(".","").replaceAll("=.+[0-9]{1}$","")+
                "\nПримерное время доставки : "+ClosestRest.replaceAll(".[А-я].+.[0-9].+=","").replaceAll(".[0-9]{1}$","")+" мин";

            return ClosestRest;



        }

        public String takeUserId() throws SQLException {


            System.out.print(UserId);
            String UserIdQuery="SELECT user_id FROM resbuild WHERE address='" + UserId + "'";
            BD.rs=BD.stmt.executeQuery(UserIdQuery);
            while (BD.rs.next()) {
                useridformbd = BD.rs.getString(1);
            }
            String IdResQuery="SELECT id_res FROM resbuild WHERE address='"+UserId+"'";
            BD.rs=BD.stmt.executeQuery(IdResQuery);
            while (BD.rs.next())
            {
                String testtask=BD.rs.getString(1);
            }
         return    useridformbd;
        }
    }











