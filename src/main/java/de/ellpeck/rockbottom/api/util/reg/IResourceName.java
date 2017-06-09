package de.ellpeck.rockbottom.api.util.reg;

public interface IResourceName{

    String getDomain();

    String getResourceName();

    boolean isEmpty();

    IResourceName addPrefix(String prefix);

    IResourceName addSuffix(String suffix);
}
