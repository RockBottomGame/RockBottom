package de.ellpeck.rockbottom.net.login;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.net.login.IUserAccount;

import java.util.UUID;

public class UserAccount implements IUserAccount {
    private String username;
    private UUID token;

    public static UserAccount login(IGameInstance game, String username, String password) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/login", new PostData("mode", "login"), new PostData("username", username), new PostData("password", password));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (obj.has("token")) {
                UserAccount account = new UserAccount(username, UUID.fromString(obj.get("token").getAsString()));
                game.loginAs(account);
                System.out.println("Logged in as " + username);
                return account;
            } else if (obj.has("error")) {
                System.err.println("Failed to login with error code " + code + ": " + obj.get("error").getAsString());
            }
        }
        return null;
    }

    public static boolean validate(UUID serverToken, String username) {
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/server_access", new PostData("mode", "check"), new PostData("token", serverToken), new PostData("username", username));
        System.out.println(obj);
        return true;
    }

    public UserAccount(String username, UUID token) {
        this.username = username;
        this.token = token;
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
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/server_access", new PostData("mode", "get"), new PostData("username", this.username), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 101) {
                return UUID.fromString(obj.get("server_access").getAsString());
            } else {
                System.err.println("Failed to get server access token with error code " + code + ": " + getError(obj));
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
        JsonObject obj = PostUtil.post("https://canitzp.de:38000/login", new PostData("mode", "renew"), new PostData("username", this.username), new PostData("token", this.token));
        if (obj.has("code")) {
            int code = obj.get("code").getAsInt();
            if (code == 101) {
                int duration = obj.get("renew_until").getAsInt();
                this.token = UUID.fromString(obj.get("token").getAsString());
                return true;
            } else {
                System.err.println("Failed to renew token with error code " + code + ". " + getError(obj));
            }
        }
        return false;
    }

    private String getError(JsonObject obj) {
        if (obj.has("error")) return obj.get("error").getAsString();
        return "No error message provided.";
    }
}
