package de.ellpeck.rockbottom.net.login;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.net.login.IUserAccount;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.data.DataManager;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

public class UserAccount implements IUserAccount {
    private String username;
    private UUID uuid;
    private UUID token;

    public static UserAccount create(IGameInstance game, String username, String password) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/login", new PostData("mode", "login"), new PostData("username", username), new PostData("password", password));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 100) {
                return new UserAccount(UUID.fromString(obj.get("uuid").getAsString()), username, UUID.fromString(obj.get("token").getAsString()));
            } else if (obj.has("error")) {
                RockBottomAPI.logger().warning("Failed to login with error code " + code + ": " + obj.get("error").getAsString());
            }
        }
        return null;
    }

    public static boolean validate(UUID serverToken, UUID uuid) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/server_access", new PostData("mode", "check"), new PostData("server_access_token", serverToken), new PostData("uuid", uuid));
        RockBottomAPI.logger().info("Validated: " + obj);
        return true;
    }

    public UserAccount(UUID uuid, String username, UUID token) {
        this.uuid = uuid;
        this.username = username;
        this.token = token;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public UUID getToken() {
        return this.token;
    }

    @Override
    public UUID getServerToken() {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/server_access", new PostData("mode", "get"), new PostData("uuid", this.uuid), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 101) {
                return UUID.fromString(obj.get("server_access").getAsString());
            } else {
                RockBottomAPI.logger().warning("Failed to get server access token with error code " + code + ": " + getError(obj));
            }
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return token != null;
    }

    @Override
    public boolean renew() {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/login", new PostData("mode", "renew"), new PostData("uuid", this.uuid), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 101) {
                int duration = obj.get("renew_until").getAsInt();
                this.token = UUID.fromString(obj.get("token").getAsString());
                RockBottomAPI.logger().info("Renewed user " + this.username + " for " + duration + "!");
                cache();
                return true;
            } else {
                RockBottomAPI.logger().warning("Failed to renew token with error code " + code + ". " + getError(obj));
            }
        }
        return false;
    }

    @Override
    public void cache() {
        File accountFile = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "account.dat");
        try {
            accountFile.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(accountFile)));

            JsonObject obj = new JsonObject();
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("username", this.username);
            obj.addProperty("token", this.token.toString());

            writer.write(Util.GSON.toJson(obj));
            writer.close();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't write game uuid", e);
        }
    }

    private String getError(JsonObject obj) {
        if (obj.has("error")) return obj.get("error").getAsString();
        return "No error message provided.";
    }

    public static UserAccount loadExisting(DataManager manager) {
        File accountFile = new File(manager.getGameDir(), "account.dat");
        if (accountFile.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(accountFile));
                JsonObject obj = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                reader.close();
                RockBottomAPI.logger().info("Read account from file");
                reader.close();
                UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
                UUID token = UUID.fromString(obj.get("token").getAsString());
                String username = obj.get("username").getAsString();
                return new UserAccount(uuid, username, token);
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't read saved account", e);
            }
        }
        return null;
    }
}
