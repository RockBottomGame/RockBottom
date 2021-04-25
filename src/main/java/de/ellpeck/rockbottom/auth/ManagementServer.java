package de.ellpeck.rockbottom.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;

public class ManagementServer {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final ManagementServer INSTANCE = new ManagementServer("https://rbm.canitzp.de");
    public static ManagementServer getServer() {
        return INSTANCE;
    }

    // TODO find another place for this
    private String apiToken;

    private final OkHttpClient httpClient;
    private final String address;

    private ManagementServer(String address) {
        this.address = address;
        this.httpClient = new OkHttpClient();
    }

    // TODO find another place for this
    public void setApiToken(String token) {
        this.apiToken = token;

        // TODO find another place for this
        File file = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "token.dat");
        try (FileWriter writer = new FileWriter(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO find another place for this
    public String getApiToken() {
        // If we already have assigned the api token, use it
        if (this.apiToken != null) {
            return this.apiToken;
        }

        // Else try and retrieve the saved token, if it exists
        File file = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "token.dat");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                this.apiToken = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Return the retrieved token, or null if the token didn't exist.
        return this.apiToken;
    }

    public void removeApiToken() {
        this.apiToken = null;
        File file = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "token.dat");
        if (file.exists()) {
            if (!file.delete()) {
                RockBottomAPI.logger().warning("Could not delete token.dat file");
            }
        }
    }

    public ServerResponse get(Endpoint endpoint, Map<String, String> headers) {
        Request request = new Request.Builder()
                .url(endpoint.appendTo(this.address))
                .headers(Headers.of(headers))
                .get().build();

        Call call = this.httpClient.newCall(request);

        ServerResponse response = new ServerResponse();
        call.enqueue(new ServerRequestCallback(response));

        RockBottomAPI.logger().info("Sending a GET request to the management server...");
        return response;
    }

    public ServerResponse post(Endpoint endpoint, JsonObject body, Map<String, String> headers) {
        Request request = new Request.Builder()
                .url(endpoint.appendTo(this.address))
                .headers(Headers.of(headers))
                .post(RequestBody.create(body.toString(), JSON_MEDIA_TYPE)).build();

        Call call = this.httpClient.newCall(request);

        ServerResponse response = new ServerResponse();
        call.enqueue(new ServerRequestCallback(response));
        RockBottomAPI.logger().info("Sending a POST request to the management server...");
        return response;
    }

    private static class ServerRequestCallback implements Callback {

        private final ServerResponse response;

        public ServerRequestCallback(ServerResponse response) {
            this.response = response;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            this.response.setResponseCode(-1);
            this.response.setInternalError(RBMError.CONNECTION_ERROR.ordinal());
            this.response.setArrived(true);
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            JsonObject body = new JsonObject();
            if (response.body().contentType().subtype().equals("json")) {
                try {
                    body = JsonParser.parseString(response.body().string()).getAsJsonObject();
                } catch (JsonSyntaxException | IllegalStateException e) {
                    if (!response.body().string().isEmpty()) {
                        e.printStackTrace();
                    }
                    body = new JsonObject();
                }
            }

            this.response.setResponseCode(response.code());
            this.response.setHeaders(response.headers().toMultimap());
            this.response.setInternalError(Integer.parseInt(response.header("RBM-Error", "-1")));
            this.response.setBody(body);
            this.response.setArrived(true);
        }
    }
}
