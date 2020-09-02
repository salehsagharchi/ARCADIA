package Game.Assets;

import Game.PlayerClasses.Player;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Item implements Cloneable, Serializable {
    private String name;
    private String description;
    private String magicCommand;
    private int price;

    public Item(String name, int price, String magicCommand, String description) {
        this.name = name.replaceAll(" ", "");
        this.description = description;
        this.magicCommand = magicCommand;
        this.price = price;
    }

    public String runMagicCommand(Player player) {
        Pattern addHP = Pattern.compile("\\+HP\\((\\d*)\\)");
        Pattern addMP = Pattern.compile("\\+MP\\((\\d*)\\)");

        String res = "";

        for (String cmd : magicCommand.split(":")) {
            cmd = cmd.trim();
            if (cmd.equals("")) {
                continue;
            }

            if (Pattern.matches(addHP.toString(), cmd)) {
                int val = Card.getNumber(cmd);
                player.addHP(val);
                res += "OK" + "\n";

            } else if (Pattern.matches(addMP.toString(), cmd)) {
                int val = Card.getNumber(cmd);
                player.addMP(val);
                res += "OK" + "\n";

            } else if (cmd.equals("NOTUSE")) {
                res += ("ERROR : " + Menus.PURPLE + "You Cannot Use \"" + name + "\" Item Now!" + Menus.RESET) + "\n";

            } else {
                res += ("ERROR : Cannot Recognize Magic Command : " + cmd) + "\n";
            }

        }
        return res.trim();
    }

    @Override
    public String toString() {
        String res = "";
        //"Item Name" Info:
        //[Item Description]

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
    public Item clone() {
        return new Item(getName(), getPrice(), getMagicCommand(), getDescription());
    }
}
