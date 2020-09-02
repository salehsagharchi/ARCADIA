package Game.Elements;

import java.io.Serializable;

public class ShopElement<T> implements Serializable {
    private T object;
    private int initValue;
    private int remain;

    public ShopElement(T object, int initValue, int remain) {
        this.object = object;
        this.initValue = initValue;
        this.remain = remain;
    }

    public T getObject() {
        return object;
    }

    public int getInitValue() {
        return initValue;
    }

    public int getRemain() {
        return remain;
    }

    public void subtractRemain(int val) {
        remain = remain - val;
    }

    public void addRemain(int val) {
        remain = remain + val;
    }
}
