package sd;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import com.google.maps.*;
import com.google.maps.model.*;
import com.squareup.okhttp.Address;
import jersey.repackaged.com.google.common.collect.Maps;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Message;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 02.02.2017.
 */
public class JsonR  {
String address;

    final Map<String, String> params = Maps.newHashMap();
    private static String ReadAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    protected  static JSONObject read(final String url) throws IOException, JSONException {
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


}

