package de.ellpeck.rockbottom.net.login;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.RockBottom;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostUtil {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static JsonObject post(String url, PostData... data) {
        StringBuilder parameters = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            parameters.append(data[i]);
            if (i < data.length - 1) {
                parameters.append("&");
            }
        }
        try {
            return post(url, parameters.toString());
        } catch (Exception e) {
            StringBuilder warning = new StringBuilder();
            warning.append("Failed to send post request to ").append(url).append("!");
            warning.append("\nData:");
            for (PostData postData : data) {
                warning.append("\nName: ").append(postData.name).append(" Value: ").append(postData.value);
            }
            RockBottomAPI.logger().warning(warning.toString());
            e.printStackTrace();
        }
        return new JsonObject();
    }

    private static JsonObject post(String url, String postData) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add Header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setConnectTimeout(1000);

        // Send Request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postData);
        wr.flush();
        wr.close();

        InputStreamReader reader = new InputStreamReader(con.getInputStream());
        JsonElement response = Util.JSON_PARSER.parse(reader);
        reader.close();

        return response.getAsJsonObject();
    }
}
