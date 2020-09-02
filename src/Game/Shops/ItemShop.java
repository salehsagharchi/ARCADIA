package Game.Shops;

import Game.Assets.Item;
import Game.Elements.ShopElement;
import Game.GameManager;
import Game.PlayerClasses.Player;
import MenuControllers.GeneralClasses.Menus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemShop implements Serializable {
    private Map<String, ShopElement<Item>> items;

    public ItemShop() {
        items = new LinkedHashMap<>();
    }

    public Map<String, ShopElement<Item>> getItems() {
        return items;
    }

    public void setItems(Map<String, ShopElement<Item>> items) {
        this.items = items;
    }

    public int getMaxItemName() {
        int res = 0;
        for (Map.Entry<String, ShopElement<Item>> entry :
                items.entrySet()) {
            Item myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }


    public ObservableList<String> getItemList(boolean showAll) {
        ObservableList<String> resItems = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxItemName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Item>> entry : items.entrySet()) {

            Item myEntry = entry.getValue().getObject();
            if (entry.getValue().getInitValue() != 0) {
                res = "GREEN;" + index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                        + myEntry.getPrice() + " : [∞]" + "\n";
                index++;
            } else {
                if (showAll) {
                    res = "RED;" + index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                            + "==> Access Denied" + "\n";
                    index++;
                }
            }
            if (res.equals("")) {
                continue;
            }
            resItems.add(res.trim());
        }
        return resItems;
    }

    public String showList(boolean showAll) {
        String res = "";
        int nSpace = getMaxItemName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Item>> entry :
                items.entrySet()) {


            Item myEntry = entry.getValue().getObject();

            if (entry.getValue().getInitValue() != 0) {
                res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                        + Menus.CYAN + myEntry.getPrice() + " : [∞]" + Menus.RESET + "\n";
                index++;
            } else {
                if (showAll) {
                    res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ')
                            + Menus.RED + "==> Access Denied" + Menus.RESET + "\n";
                    index++;
                }
            }


        }
        return res.trim();
    }


    public String addItem(Item item) {
        if (item.getPrice() != -1) {
            items.put(item.getName(), new ShopElement<>(item, -1, -1));
        } else {
            items.put(item.getName(), new ShopElement<>(item, 0, 0));
        }
        return "OK";
    }

    public String buyItem(String nameString, String numString, GameManager manager, Player player) {
        if (findItem(nameString) == null) {
            return "ERROR : Item Name";
        }
        ShopElement<Item> itemElement = findItemElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);
        if (itemElement.getInitValue() == 0) {
            return "You Cannot Buy \"" + itemElement.getObject().getName() + "\" Item!";
        }
        // item remain is unlimited
//        if (itemElement.getRemain() - num < 0) {
//            return "Remain is not Enough !";
//        }
        if (player.getGil() < itemElement.getObject().getPrice() * num) {
            return "Not Enough Gil!";
        }
        player.getInventory().addItem(itemElement.getObject().getName(), num);
        player.subtractGil(itemElement.getObject().getPrice() * num);
        return "Successfully Bought " + num + " of \"" + itemElement.getObject().getName() + "\"!";
    }

    public String sellItem(String nameString, String numString, GameManager manager, Player player) {
        if (player.getInventory().findElementNumber(player.getInventory().getItems(), nameString) == -2) {
            return "ERROR : Item Name";
        }
        ShopElement<Item> itemElement = findItemElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);

        if (player.getInventory().findElementNumber(player.getInventory().getItems(), nameString) - num < 0) {
            return "Not Enough Item!";
        }
        if (itemElement.getInitValue() == 0) {
            return "You Cannot Sell \"" + itemElement.getObject().getName() + "\" Item!";
        }
        player.getInventory().removeItem(itemElement.getObject().getName(), num);
        player.addGil(itemElement.getObject().getPrice() * num / 2);
        return "Successfully Sold " + num + " of \"" + itemElement.getObject().getName() + "\"!";
    }

    public ShopElement<Item> findItemElement(String key) {
        return items.getOrDefault(key, null);
    }

    public Item findItem(String key) {
        if (items.containsKey(key)) {
            return items.get(key).getObject();
        } else {
            return null;
        }
    }

    public int findItemInitValue(String key) {
        if (items.containsKey(key)) {
            return items.get(key).getInitValue();
        } else {
            return -2;
        }
    }

    public int findItemRemain(String key) {
        if (items.containsKey(key)) {
            return items.get(key).getRemain();
        } else {
            return -2;
        }
    }

    public void clearItems() {
        items.clear();
    }


}
