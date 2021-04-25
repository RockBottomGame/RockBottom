package de.ellpeck.rockbottom.auth;

public enum Endpoint {
    GET_USER("/user"),
    CREATE_USER("/user/create"),
    LOGIN_USER("/user/login"),
    FORGOT_PASSWORD("/user/password_reset"),
    RESET_PASSWORD("/user/password_reset_set"),
    SET_USERNAME("/user/username"),
    SET_PASSWORD("/user/password"),
    SET_DESIGN("/user/player_design"),
    VERIFY("/user/verify"),
    VERIFY_RESEND("/user/resend_verification_code");

    public final String value;

    Endpoint(String value) {
        this.value = value;
    }

    public String appendTo(String address) {
        return address + this.value;
    }
}
