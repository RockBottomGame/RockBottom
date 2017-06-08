package de.ellpeck.rockbottom.api.util.reg;

import java.util.Map;

public interface IRegistry<T, U>{

    void register(T id, U value);

    U get(T id);

    T getId(U value);

    int getSize();

    Map<T, U> getUnmodifiable();

}
