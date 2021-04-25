package de.ellpeck.rockbottom.auth;

public enum RBMError {
    INVALID_INTERNAL_CODE(0, "info.server.invalid_error"),
    CONNECTION_ERROR(1, "info.server.failed_connection"),

    MISSING_RESOURCE(100, "unused"),
    MISSING_JSON(101, "info.server.missing_json"),
    MISSING_API_KEY(102, "info.server.missing_api_key"),
    MISSING_EMAIL(110, "info.server.missing_email"),
    MISSING_PASSWORD(111, "info.server.missing_password"),
    MISSING_USERNAME(112, "info.server.missing_username"),
    MISSING_DESIGN(113, "info.server.missing_player_design"),
    MISSING_OLD_PASSWORD(190, "info.server.missing_old_password"),

    REGEX_ERROR(200, "unused"),
    EMAIL_REGEX(210, "info.server.invalid_email"),
    PASSWORD_REGEX(211, "info.server.invalid_password"),
    USERNAME_REGEX(212, "info.server.invalid_username"),

    RESOURCE_TAKEN(300, "unused"),
    EMAIL_TAKEN(310, "info.server.email_taken"),
    USERNAME_TAKEN(312, "info.server.username_taken"),

    INVALID_GENERIC(400, "unused"),
    INVALID_CREDENTIALS(401, "info.server.invalid_credentials"),
    INVALID_API_KEY(402, "info.server.invalid_token"),
    INVALID_EMAIL(410, "unused"),
    INVALID_PASSWORD(411, "info.server.incorrect_password"),
    INVALID_PLAYER_DESIGN(413, "info.server.invalid_design_json"),
    INVALID_VERIFICATION_CODE(490, "info.server.invalid_verification_code");

    public final int code;
    public final String message;

    RBMError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RBMError fromErrorCode(int code) {
        if (code == -1) {
            return null;
        }

        for (RBMError internalErrorCode : RBMError.values()) {
            if (internalErrorCode.code == code) {
                return internalErrorCode;
            }
        }

        return INVALID_INTERNAL_CODE;
    }
}
