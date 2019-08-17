package de.ellpeck.rockbottom.net.post;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostUtil {

    private static final String USER_AGENT = "Mozilla/5.0";

    // TODO: async
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
            System.err.println("Failed to send post request. Printing stack trace...");
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

        // Send Request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        InputStreamReader reader = new InputStreamReader(con.getInputStream());
        JsonElement response = Util.JSON_PARSER.parse(reader);
        reader.close();

        return response.getAsJsonObject();
    }
}
