package de.ellpeck.rockbottom.net.login;

import java.util.UUID;

public class PostData {
    public final String name;
    public final String value;

    public PostData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public PostData(String name, double value) {
        this(name, Double.toString(value));
    }

    public PostData(String name, UUID value) {
        this(name, value.toString());
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
