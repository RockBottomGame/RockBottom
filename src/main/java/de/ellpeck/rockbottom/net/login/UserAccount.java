package de.ellpeck.rockbottom.net.login;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.login.IUserAccount;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.data.DataManager;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

public class UserAccount implements IUserAccount {
    private final String email;
    private final UUID uuid;

    private String username;
    private UUID token;

    public static boolean validate(UUID serverToken, UUID uuid) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "sa_check"), new PostData("server_access_token", serverToken), new PostData("uuid", uuid));
        RockBottomAPI.logger().info("Validated: " + obj);
        return true;
    }

    public UserAccount(UUID uuid, String email, UUID token) {
        this.uuid = uuid;
        this.email = email;

        // TODO: fetch username and player design
        this.username = email;
        this.token = token;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getEmail() {
        return this.email;
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
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "sa_get"), new PostData("uuid", this.uuid), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 210) { // Successful Token
                return UUID.fromString(obj.get("server_access").getAsString());
            } else {
                RockBottomAPI.logger().warning("Failed to get server access token with error code " + code + ": " + getMessage(obj));
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
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "renew"), new PostData("uuid", this.uuid), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 202) { // 202 = Renew Success
                int duration = obj.get("renew_until").getAsInt();
                this.token = UUID.fromString(obj.get("token").getAsString());
                RockBottomAPI.logger().info("Renewed user " + this.email + " for " + duration + "!");
                cache();
                return true;
            } else if (code == 312) { // 312 = Invalid User
                return false;
            } else {
                RockBottomAPI.logger().warning("Failed to renew token with error code " + code + ". " + getMessage(obj));
            }
        }
        return false;
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/", new PostData("mode", "set_password"), new PostData("token", this.getToken()), new PostData("uuid", this.getUUID()), new PostData("old_password", oldPassword), new PostData("new_password", newPassword));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 211) { // Password change success
                RockBottomAPI.logger().info("Changed password for " + this.email + " successfully!");
                return true;
            } else {
                RockBottomAPI.logger().warning("Failed to change password with error code " + code + ": " + getMessage(obj));
            }
        }
        return true;
    }

    @Override
    public void cache() {
        File accountFile = new File(RockBottomAPI.getGame().getDataManager().getGameDir(), "account.dat");
        try {
            accountFile.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(accountFile)));

            JsonObject obj = new JsonObject();
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("username", this.email);
            obj.addProperty("token", this.token.toString());

            writer.write(Util.GSON.toJson(obj));
            writer.close();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't write game uuid", e);
        }
    }

    private String getMessage(JsonObject obj) {
        if (obj.has("message")) return obj.get("message").getAsString();
        return "No message provided.";
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
