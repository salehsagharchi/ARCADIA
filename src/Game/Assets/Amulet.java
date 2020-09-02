package Game.Assets;

import java.io.Serializable;

public class Amulet implements Cloneable, Serializable {
    private String name;
    private String description;
    private String magicCommand;
    private int price;

    public Amulet(String name, int price, String magicCommand, String description) {
        this.name = name.replaceAll(" ", "");
        this.description = description;
        this.magicCommand = magicCommand;
        this.price = price;
    }

    @Override
    public String toString() {
        String res = "";
        //"Amulet Name" Info:
        //[Amulet Description]

        res += "\"" + getName() + "\" Info:";

        if (!getDescription().equals("")) {
            res += "\n" + getDescription();
        }
        return res.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getMagicCommand() {
        return magicCommand;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public Amulet clone() {
        return new Amulet(getName(), getPrice(), getMagicCommand(), getDescription());
    }
}
