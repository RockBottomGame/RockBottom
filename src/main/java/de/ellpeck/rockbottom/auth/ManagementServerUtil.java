package de.ellpeck.rockbottom.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.GameAccount;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ManagementServerUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("[ \\-0-9A-Z_a-}À-ÖØ-öø-ÿ]{3,16}");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("[^\\s]{8,}");
    private static final Pattern VERIFICATION_PATTERN = Pattern.compile("[\\d]{6}");
    private static final String CODE_ERROR = "info.server.invalid_code";

    public static ServerResponse getUser(String apiToken, Consumer<GameAccount> onSuccess, Consumer<String> onFailed) {
        return ManagementServer.getServer().get(Endpoint.GET_USER, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept(GameAccount.from(sr.getBody()));
                    }
                });
    }

    public static ServerResponse createUser(String email, String username, String password, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!emailMatches(email)) {
            onFailed.accept(RBMError.EMAIL_REGEX.message);
            return null;
        }
        if (!usernameMatches(username)) {
            onFailed.accept(RBMError.USERNAME_REGEX.message);
            return null;
        }
        if (!passwordMatches(password)) {
            onFailed.accept(RBMError.PASSWORD_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("e-mail", email);
        details.addProperty("username", username);
        details.addProperty("password", password);
        return ManagementServer.getServer().post(Endpoint.CREATE_USER, details, header().toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.created_user");
                    }
                });
    }

    public static ServerResponse loginUser(String email, String password, Consumer<GameAccount> onSuccess, Consumer<String> onFailed) {
        if (!emailMatches(email)) {
            onFailed.accept(RBMError.EMAIL_REGEX.message);
            return null;
        }
        if (!passwordMatches(password)) {
            onFailed.accept(RBMError.PASSWORD_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("e-mail", email);
        details.addProperty("password", password);
        return ManagementServer.getServer().post(Endpoint.LOGIN_USER, details, header().toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        ManagementServer.getServer().setApiToken(sr.getBody().get("api-key").getAsString());
                        onSuccess.accept(GameAccount.from(sr.getBody().getAsJsonObject("account")));
                    }
                });
    }

    public static ServerResponse setUsername(String apiToken, String username, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!usernameMatches(username)) {
            onFailed.accept(RBMError.USERNAME_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("username", username);
        return ManagementServer.getServer().post(Endpoint.SET_USERNAME, details, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.set_username");
                    }
                });
    }

    public static ServerResponse setPassword(String apiToken, String oldPassword, String password, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!passwordMatches(password)) {
            onFailed.accept(RBMError.PASSWORD_REGEX.message);
            return null;
        }
        if (!passwordMatches(oldPassword)) {
            onFailed.accept(RBMError.PASSWORD_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("old_password", oldPassword);
        details.addProperty("password", password);
        return ManagementServer.getServer().post(Endpoint.SET_PASSWORD, details, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.set_password");
                    }
                });
    }

    public static ServerResponse setPlayerDesign(String apiToken, IPlayerDesign design, Consumer<String> onSuccess, Consumer<String> onFailed) {
        JsonObject details = new JsonObject();
        details.add("player_design", JsonParser.parseString(Util.GSON.toJson(design)).getAsJsonObject());
        return ManagementServer.getServer().post(Endpoint.SET_DESIGN, details, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.set_design");
                    }
                });
    }

    public static ServerResponse verify(String apiToken, String code, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!verificationCodeMatches(code)) {
            onFailed.accept(CODE_ERROR);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("verification_code", code);
        return ManagementServer.getServer().post(Endpoint.VERIFY, details, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.verified");
                    }
                });
    }

    public static ServerResponse resendCode(String apiToken, Consumer<String> onSuccess, Consumer<String> onFailed) {
        JsonObject details = new JsonObject();
        return ManagementServer.getServer().post(Endpoint.VERIFY_RESEND, details, header().withApiToken(apiToken).toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.verify_resent");
                    }
                });
    }

    public static ServerResponse requestPasswordReset(String email, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!emailMatches(email)) {
            onFailed.accept(RBMError.EMAIL_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("e-mail", email);
        return ManagementServer.getServer().post(Endpoint.FORGOT_PASSWORD, details, header().toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.password_reset_requested");
                    }
                });
    }

    public static ServerResponse resetPassword(String email, String code, String password, Consumer<String> onSuccess, Consumer<String> onFailed) {
        if (!emailMatches(email)) {
            onFailed.accept(RBMError.EMAIL_REGEX.message);
            return null;
        }
        if (!verificationCodeMatches(code)) {
            onFailed.accept(CODE_ERROR);
            return null;
        }
        if (!passwordMatches(password)) {
            onFailed.accept(RBMError.PASSWORD_REGEX.message);
            return null;
        }
        JsonObject details = new JsonObject();
        details.addProperty("e-mail", email);
        details.addProperty("verification_code", code);
        details.addProperty("password", password);

        return ManagementServer.getServer().post(Endpoint.RESET_PASSWORD, details, header().toMap())
                .onArrived((sr) -> {
                    if (sr.hasFailed()) {
                        RBMError error = handleError(sr);
                        onFailed.accept(error.message);
                    } else {
                        onSuccess.accept("info.server.password_reset");
                    }
                });
    }

    private static boolean emailMatches(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean usernameMatches(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    private static boolean passwordMatches(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private static boolean verificationCodeMatches(String code) {
        return VERIFICATION_PATTERN.matcher(code).matches();
    }

    private static RBMError handleError(ServerResponse response) {
        RBMError error = response.getRBMError();
        if (error == RBMError.INVALID_INTERNAL_CODE) {
            RockBottomAPI.logger().warning("Unhandled exception response from the management server. " + error.name());
            RockBottomAPI.logger().warning("Error code: " + response.getResponseCode() + ", RBMError: " + response.getRBMErrorCode());
        }
        if (response.getException() != null) {
            RockBottomAPI.logger().warning("Unhandled exception response from the management server. " + error.name());
            RockBottomAPI.logger().warning("Error code: " + response.getResponseCode() + ", RBMError: " + error.code);
            response.getException().printStackTrace();
        }

        return error;
    }

    private static HeaderBuilder header() {
        return new HeaderBuilder();
    }

    private static class HeaderBuilder {

        private final Map<String, String> headers;

        private HeaderBuilder() {
            this.headers = new HashMap<>();
        }

        public HeaderBuilder with(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HeaderBuilder withApiToken(String token) {
            this.headers.put("X-API-Key", token);
            return this;
        }

        public Map<String, String> toMap() {
            return this.headers;
        }

    }

}
