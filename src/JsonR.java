
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Interner;
import com.google.common.collect.Iterables;

import com.google.maps.*;
import com.google.maps.model.*;
import com.squareup.okhttp.Address;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 02.02.2017.
 */

public class JsonR  {
    private String ClientAddress;
    private String RestAddress;
    private double MinDistance;
    private final Map<String, String> params = Maps.newHashMap();
    private HashMap<String, Double> Distance = new HashMap<>();
    private Set<Map.Entry<String, Double>> entrySet = Distance.entrySet();
    private String ClosestRest;
    private String AdressClosestRest;
    private String IdRest;
    private String user_id;
    private String RestName;
    private Object[] DistanceValues;
    private static final String API_KEY ="AIzaSyBmsR-jsIN74P-KwHdwzh37rzwBCrQICvU";
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

    public String makeURL(Message message) {
        params.put("address", "Ростовская область, Ростов-на-Дону " + message.getText());
        params.put("language", "ru");
        String baseURL = "http://maps.googleapis.com/maps/api/geocode/json";
        final String url = baseURL + '?' + encode(params);
        try {
            final JSONObject response = JsonR.read(url);
            if (response.getJSONArray("results").length() == 0) { //обработка не существующей улицы
                ClientAddress = "Такой улицы нет или сервер не отвечает. Введите адрес заново";
            } else {
                JSONObject location = response.getJSONArray("results").getJSONObject(0);
                ClientAddress = location.getString("formatted_address");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return ClientAddress;
    }

    public String chooseClosRest(String ClientAddress, String RestId, double MaxTime) throws IOException, SQLException {
        String addressQuery = "SELECT resbuild.address,res.name FROM resbuild,res WHERE res.id=resbuild.id_res and resbuild.id_res='"+RestId+"'";
        try {
            BD.rs = BD.stmt.executeQuery(addressQuery);
            while (BD.rs.next()) {
                RestName = BD.rs.getString(2);
                RestAddress = BD.rs.getString(1).replace(" ", "_").replace(",", "_");
                String url = new String("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + ClientAddress.replace(" ", "_").replace(",", "_") + "&destinations=" +
                        RestAddress+"&traffic_model&departure_time=now&key="+API_KEY) + encode(params);
                final JSONObject response = JsonR.read(url);
                //время в пути без учета пробок между рестораном и адресом клиента
                String timebetween2loc = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration_in_traffic").getString("text").replace("mins", "").replace("min", "").trim();
                double TimetoRest = Double.parseDouble(timebetween2loc) + MaxTime;
                Distance.put(RestAddress, TimetoRest);
                DistanceValues = Distance.values().toArray();
                MinDistance = (double) Distance.values().toArray()[0];
                for (int i = 0; i < DistanceValues.length; i++) {
                    if (MinDistance > (double) DistanceValues[i]) {
                        MinDistance = (double) DistanceValues[i];
                    }
                }

                Object KeytoRest = MinDistance;
                for (Map.Entry<String, Double> pair : entrySet) {
                    if (KeytoRest.equals(pair.getValue())) {
                        ClosestRest = pair.toString();
                    }
                }
                AdressClosestRest = ClosestRest.replaceAll("=.[0-9]+\\.+[0-9]", "").replaceAll("=.*", "").replace("_", " ").replace(".  ", "., ");
                ClosestRest = "Ближайщий к вам ресторан " + RestName + " находится на " + ClosestRest.replace("_", " ").replace(".", "").replaceAll("=.+[0-9]{1}$", "") +
                        "\nПримерное время доставки : " + ClosestRest.replaceAll(".[А-я].+.[0-9].+=", "").replaceAll(".[0-9]{1}$", "") + " мин";

            }
            entrySet.clear();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return ClosestRest;
    }

    public String takeIdRest() throws SQLException {
        String IdResQuery = "SELECT id_res FROM resbuild WHERE address='" + AdressClosestRest + "'";
        BD.rs = BD.stmt.executeQuery(IdResQuery);
        while (BD.rs.next()) {
            IdRest = BD.rs.getString(1);
        }
        return IdRest;
    }

    public String takeUserIdFromMessage(Message message) {
        String fullmsg = message.toString();
        Pattern p = Pattern.compile("id=[0-9]+,");
        Matcher m = p.matcher(fullmsg);
        if (m.find()) {
            user_id = fullmsg.substring(m.start() + 3, m.end() - 1);
        }
        return user_id;
    }
}











