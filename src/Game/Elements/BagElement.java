package Game.Elements;

import java.io.Serializable;

public class BagElement<T> implements Serializable {
    private T object;
    private int number;

    public BagElement(T object, int number) {
        this.object = object;
        this.number = number;
    }

    public T getObject() {
        return object;
    }

    public int getNumber() {
        return number;
    }

}
