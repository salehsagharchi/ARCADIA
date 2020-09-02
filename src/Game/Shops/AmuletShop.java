package Game.Shops;

import Game.Assets.Amulet;
import Game.Elements.ShopElement;
import Game.GameManager;
import Game.PlayerClasses.Player;
import MenuControllers.GeneralClasses.Menus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AmuletShop implements Serializable {
    private Map<String, ShopElement<Amulet>> amulets;

    public AmuletShop() {
        amulets = new LinkedHashMap<>();
    }

    public Map<String, ShopElement<Amulet>> getAmulets() {
        return amulets;
    }

    public void setAmulets(Map<String, ShopElement<Amulet>> amulets) {
        this.amulets = amulets;
    }

    public int getMaxAmuletName() {
        int res = 0;
        for (Map.Entry<String, ShopElement<Amulet>> entry :
                amulets.entrySet()) {
            Amulet myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }

    public ObservableList<String> getAmuletList(boolean showAll) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxAmuletName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Amulet>> entry :
                amulets.entrySet()) {

            Amulet myEntry = entry.getValue().getObject();
            if (entry.getValue().getRemain() != 0) {
                res = "GREEN;" + index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                        + myEntry.getPrice() + " : [" + entry.getValue().getRemain() + "]" + "\n";
                index++;
            } else {
                if (entry.getValue().getInitValue() != 0) {
                    res = "RED;" + index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                            + "==> Remain = 0" + "\n";
                    index++;
                } else {
                    if (showAll) {
                        res = "RED;" + index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                                + "==> Access Denied" + "\n";
                        index++;
                    }
                }
            }
            if (res.equals("")) {
                continue;
            }
            items.add(res.trim());
        }
        return items;
    }

    public String showList(boolean showAll) {
        String res = "";
        int nSpace = getMaxAmuletName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Amulet>> entry :
                amulets.entrySet()) {


            Amulet myEntry = entry.getValue().getObject();

            if (entry.getValue().getRemain() != 0) {
                res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                        + Menus.CYAN + myEntry.getPrice() + " : [" + entry.getValue().getRemain() + "]" + Menus.RESET + "\n";
                index++;
            } else {
                if (entry.getValue().getInitValue() != 0) {
                    res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                            + Menus.RED + "==> Remain = 0" + Menus.RESET + "\n";
                    index++;
                } else {
                    if (showAll) {
                        res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                                + Menus.RED + "==> Access Denied" + Menus.RESET + "\n";
                        index++;
                    }
                }
            }


        }
        return res.trim();
    }

    public String addAmulet(Amulet amulet) {
        if (amulet.getPrice() != -1) {
            amulets.put(amulet.getName(), new ShopElement<>(amulet, 1, 1));
        } else {
            amulets.put(amulet.getName(), new ShopElement<>(amulet, 0, 0));
        }
        return "OK";
    }

    public String buyAmulet(String nameString, String numString, GameManager manager, Player player) {
        if (findAmulet(nameString) == null) {
            return "ERROR : Amulet Name";
        }
        ShopElement<Amulet> amuletElement = findAmuletElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);
        if (amuletElement.getInitValue() == 0) {
            return "You Cannot Buy \"" + amuletElement.getObject().getName() + "\" Amulet!";
        }
        if (amuletElement.getRemain() - num < 0) {
            return "Remain is not Enough !";
        }
        if (player.getGil() < amuletElement.getObject().getPrice() * num) {
            return "Not Enough Gil!";
        }
        amuletElement.subtractRemain(num);
        player.getInventory().addAmulet(amuletElement.getObject().getName(), num);
        player.subtractGil(amuletElement.getObject().getPrice() * num);
        return "Successfully Bought " + num + " of \"" + amuletElement.getObject().getName() + "\"!";
    }

    public String sellAmulet(String nameString, String numString, GameManager manager, Player player) {
        if (player.getInventory().findElementNumber(player.getInventory().getAmulets(), nameString) == -2) {
            return "ERROR : Amulet Name";
        }
        ShopElement<Amulet> amuletElement = findAmuletElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);
        if (player.getBag().equippedAmulet().trim().equals(amuletElement.getObject().getName())) {
            return "You Cannot Sell Your Equipped Amulet!";
        }
        if (player.getInventory().findElementNumber(player.getInventory().getAmulets(), nameString) - num < 0) {
            return "Not Enough Amulet!";
        }
        if (amuletElement.getInitValue() == 0) {
            return "You Cannot Sell \"" + amuletElement.getObject().getName() + "\" Amulet!";
        }
        amuletElement.addRemain(num);
        player.getInventory().removeAmulet(amuletElement.getObject().getName(), num);
        player.addGil(amuletElement.getObject().getPrice() * num / 2);
        return "Successfully Sold " + num + " of \"" + amuletElement.getObject().getName() + "\"!";
    }


    public ShopElement<Amulet> findAmuletElement(String key) {
        return amulets.getOrDefault(key, null);
    }

    public Amulet findAmulet(String key) {
        if (amulets.containsKey(key)) {
            return amulets.get(key).getObject();
        } else {
            return null;
        }
    }

    public int findAmuletInitValue(String key) {
        if (amulets.containsKey(key)) {
            return amulets.get(key).getInitValue();
        } else {
            return -2;
        }
    }

    public int findAmuletRemain(String key) {
        if (amulets.containsKey(key)) {
            return amulets.get(key).getRemain();
        } else {
            return -2;
        }
    }

    public void clearAmulets() {
        amulets.clear();
    }
}
