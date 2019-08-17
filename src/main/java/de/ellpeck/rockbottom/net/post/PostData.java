package de.ellpeck.rockbottom.net.post;

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

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
