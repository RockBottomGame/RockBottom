package de.ellpeck.rockbottom.auth;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Defines a class representing an awaiting response from the management server.
 * Any getters from this class are invalid until the response {@link ServerResponse#hasArrived() hasArrived}
 * and should not be access until then.
 */
public class ServerResponse {

    private boolean arrived;
    private Consumer<ServerResponse> onArrived;

    private int responseCode;
    private Map<String, List<String>> headers;
    private JsonObject body;

    private RBMError rbmError;
    private int rbmErrorCode;
    private Exception exception;

    /**
     * Executes the given consumer once the message has been set to have arrived.
     * @param onArrived To execute one a response from the server has arrived. Contains this {@link ServerResponse response}.
     */
    public ServerResponse onArrived(Consumer<ServerResponse> onArrived) {
        this.onArrived = onArrived;

        if (this.hasArrived()) {
            onArrived.accept(this);
        }

        return this;
    }

    public boolean hasArrived() {
        return this.arrived;
    }

    public boolean hasFailed() {
        return this.rbmError != null;
    }

    public JsonObject getBody() {
        return this.body;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public RBMError getRBMError() {
        return this.rbmError;
    }

    void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    void setBody(JsonObject body) {
        this.body = body;
    }

    void setInternalError(int code) {
        this.rbmError = RBMError.fromErrorCode(code);
        this.rbmErrorCode = code;
    }

    void setArrived(boolean arrived) {
        this.arrived = arrived;

        if (this.onArrived != null) {
            this.onArrived.accept(this);
        }
    }

    void setException(Exception e) {
        this.exception = e;
    }

    private void checkAccess() {
        if (!this.hasArrived()) {
            throw ManagementServerException.NOT_YET_ARRIVED;
        }
    }

    public Exception getException() {
        return this.exception;
    }

    public int getRBMErrorCode() {
        return this.rbmErrorCode;
    }
}
