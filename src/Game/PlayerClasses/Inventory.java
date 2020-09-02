package Game.PlayerClasses;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import Game.Constants.Initializer_Statics;
import Game.Elements.BagElement;
import MenuControllers.GeneralClasses.Menus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inventory implements Serializable {
    private Map<String, BagElement<Card>> cards;
    private Map<String, BagElement<Item>> items;
    private Map<String, BagElement<Amulet>> amulets;
    private Player player;

    public Inventory(boolean defaultElements, Player player) {

        if (defaultElements) {
            items = Initializer_Statics.requestDefaultItems();
            cards = Initializer_Statics.requestDefaultCards();
            amulets = Initializer_Statics.requestDefaultAmulets();
        } else {
            cards = new LinkedHashMap<>();
            items = new LinkedHashMap<>();
            amulets = new LinkedHashMap<>();
        }

        this.player = player;
    }

    public String addCardToDeck(String nameString, String index) {
        if (findElement(cards, nameString) == null) {
            return "ERROR : Card Name";
        }
        BagElement<Card> cardElement = findBagElement(cards, nameString);
        String name = cardElement.getObject().getName();
        try {
            if (Integer.parseInt(index) <= 0 || Integer.parseInt(index) > player.getBag().getDeck().getLimit()) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int slot = Integer.parseInt(index);
        if (cardElement.getNumber() <= player.getBag().getDeck().findCardNumbers(name)) {
            return "Your Deck is Fulled With " + name + " . Not Another Card!";
        }
        player.getBag().getDeck().addCardToSlot(cardElement.getObject().clone(), slot);
        return "Successfully " + "\"" + name + "\"" + " was Added to Slot " + slot;
    }

    public String removeCardFromDeck(String index) {
        try {
            if (Integer.parseInt(index) <= 0 || Integer.parseInt(index) > player.getBag().getDeck().getLimit()) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int slot = Integer.parseInt(index);
        if (player.getBag().getDeck().getSlotCard(slot) == null) {
            return "Slot " + slot + " is Empty!";
        }
        String nameCard = player.getBag().getDeck().getSlotName(slot);
        player.getBag().getDeck().removeCard(slot);
        return "Successfully " + "\"" + nameCard + "\"" + " was Removed From Your Slot " + slot;
    }

    public String equipAmulet(String name) {
        if (findElementNumber(amulets, name) == -2) {
            return "ERROR : Amulet Name";
        }
        BagElement<Amulet> amuletElement = findBagElement(amulets, name);
        if (player.getBag().equippedAmulet().trim().equals(amuletElement.getObject().getName())) {
            return "Your Equipped Amulet is " + amuletElement.getObject().getName() + " now!";
        }
        player.getBag().setAmulet(Initializer_Statics.getCloneOfAmulet(amuletElement.getObject().getName()));
        return "Successfully " + amuletElement.getObject().getName() + " was Equipped On Your Player.";
    }

    public String removeEquippedAmulet() {
        if (player.getBag().getAmulet() == null) {
            return "Your Player Equipped Amulet is NULL!";
        }
        player.getBag().setAmulet(null);
        return "Successfully Your Player Equipped Amulet was Removed.";
    }

    public int getMaxAmuletName() {
        int res = 0;
        for (Map.Entry<String, BagElement<Amulet>> entry :
                amulets.entrySet()) {
            Amulet myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }

    public int getMaxItemName() {
        int res = 0;
        for (Map.Entry<String, BagElement<Item>> entry :
                items.entrySet()) {
            Item myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }

    public int getMaxCardName() {
        int res = 0;
        for (Map.Entry<String, BagElement<Card>> entry :
                cards.entrySet()) {
            Card myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }

    public ObservableList<String> getListOfCard() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxCardName();
        int index = 1;
        for (Map.Entry<String, BagElement<Card>> entry :
                cards.entrySet()) {
            Card myEntry = entry.getValue().getObject();
            res = index + ".\t\""
                    + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ');
            res += "[" + entry.getValue().getNumber()
                    + " / " + player.getBag().getDeck().findCardNumbers(myEntry.getName()) + "]";
            if (player.getBag().getDeck().findCardNumbers(myEntry.getName()) == entry.getValue().getNumber()) {
                res = "PURPLE;" + res;
            } else {
                res = "GREEN;" + res;
            }
            index++;
            resultList.add(res.trim());
        }
        if (resultList.size() == 0) {
            resultList.add("RED;\t" + "--> Not Any Item Now!");
        }
        return resultList;
    }

    public String showListOfCards() {
        String res = "";
        int nSpace = getMaxCardName();
        int index = 1;
        for (Map.Entry<String, BagElement<Card>> entry :
                cards.entrySet()) {
            Card myEntry = entry.getValue().getObject();
            res += index + ".\t\""
                    + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ');
            if (player.getBag().getDeck().findCardNumbers(myEntry.getName()) == entry.getValue().getNumber()) {
                res += Menus.PURPLE;
            } else {
                res += Menus.CYAN;
            }
            res += "[" + entry.getValue().getNumber()
                    + " / " + player.getBag().getDeck().findCardNumbers(myEntry.getName()) + "]" + Menus.RESET + "\n";
            index++;
        }
        if (res.trim().equals("")) {
            return Menus.RED + "\t" + "--> Not Any Item Now!" + Menus.RESET;
        } else {
            res = "::\t\"CARD NAME\"" + new String(new char[nSpace - ("CARD NAME").length() + 6]).replace('\0', ' ')
                    + "[ALL/ONDECK]\n\n" + res;
        }
        return res.trim();
    }

    public ObservableList<String> getListOfItems() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxItemName();
        int index = 1;
        for (Map.Entry<String, BagElement<Item>> entry :
                items.entrySet()) {
            Item myEntry = entry.getValue().getObject();
            res = index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ') + entry.getValue().getNumber() + "\n";
            index++;
            resultList.add(res.trim());
        }
        if (resultList.size() == 0) {
            resultList.add("RED;\t" + "--> Not Any Item Now!");
        }
        return resultList;
    }

    public String showListOfItems() {
        String res = "";
        int nSpace = getMaxItemName();
        int index = 1;
        for (Map.Entry<String, BagElement<Item>> entry :
                items.entrySet()) {
            Item myEntry = entry.getValue().getObject();
            res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ') + entry.getValue().getNumber() + "\n";
            index++;
        }
        if (res.trim().equals("")) {
            return Menus.RED + "\t" + "--> Not Any Item Now!" + Menus.RESET;
        }
        return res.trim();
    }

    public ObservableList<String> getListOfAmulets() {
        ObservableList<String> resultList = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxAmuletName();
        int index = 1;
        for (Map.Entry<String, BagElement<Amulet>> entry :
                amulets.entrySet()) {
            Amulet myEntry = entry.getValue().getObject();
            res = index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ') + entry.getValue().getNumber() + "\n";
            index++;
            resultList.add(res.trim());
        }
        if (resultList.size() == 0) {
            resultList.add("RED;\t" + "--> Not Any Item Now!");
        }
        return resultList;
    }

    public String showListOfAmulets() {
        String res = "";
        int nSpace = getMaxAmuletName();
        int index = 1;
        for (Map.Entry<String, BagElement<Amulet>> entry :
                amulets.entrySet()) {
            Amulet myEntry = entry.getValue().getObject();
            res += index + ".\t\"" + myEntry.getName() + "\"" + new String(new char[nSpace - myEntry.getName().length() + 6]).replace('\0', ' ') + entry.getValue().getNumber() + "\n";
            index++;
        }
        if (res.trim().equals("")) {
            return Menus.RED + "\t" + "--> Not Any Item Now!" + Menus.RESET;
        }
        return res.trim();
    }


    public String addCard(String name, int value) {
        if (cards.containsKey(name)) {
            cards.put(name, new BagElement<>(findElement(cards, name), findElementNumber(cards, name) + value));
        } else {
            cards.put(name, new BagElement<>(Initializer_Statics.getCloneOfCard(name), value));
        }
        return "";
    }

    public String removeCard(String name, int value) {
        if (cards.containsKey(name)) {
            int nowNumber = findElementNumber(cards, name) - value;
            if (nowNumber == 0) {
                // REMOVE
                cards.remove(name);
            } else if (nowNumber > 0) {
                // Subtract
                cards.put(name, new BagElement<>(findElement(cards, name), nowNumber));
            } else {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }
        return "";
    }

    public String addItem(String name, int num) {
        if (items.containsKey(name)) {
            items.put(name, new BagElement<>(findElement(items, name), findElementNumber(items, name) + num));
        } else {
            items.put(name, new BagElement<>(Initializer_Statics.getCloneOfItem(name), num));
        }
        return "";
    }

    public String removeItem(String name, int num) {
        if (items.containsKey(name)) {
            int nowNumber = findElementNumber(items, name) - num;
            if (nowNumber == 0) {
                // REMOVE
                items.remove(name);
            } else if (nowNumber > 0) {
                // Subtract
                items.put(name, new BagElement<>(findElement(items, name), nowNumber));
            } else {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }
        return "";
    }

    public String addAmulet(String name, int num) {
        if (amulets.containsKey(name)) {
            amulets.put(name, new BagElement<>(findElement(amulets, name), findElementNumber(amulets, name) + num));
        } else {
            amulets.put(name, new BagElement<>(Initializer_Statics.getCloneOfAmulet(name), num));
        }
        return "";
    }

    public String removeAmulet(String name, int num) {
        if (amulets.containsKey(name)) {
            int nowNumber = findElementNumber(amulets, name) - num;
            if (nowNumber == 0) {
                // REMOVE
                amulets.remove(name);
            } else if (nowNumber > 0) {
                // Subtract
                amulets.put(name, new BagElement<>(findElement(amulets, name), nowNumber));
            } else {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }
        return "";
    }

    public <T> BagElement<T> findBagElement(Map<String, BagElement<T>> list, String key) {
        return list.getOrDefault(key, null);
    }

    public <T> T findElement(Map<String, BagElement<T>> list, String key) {
        if (list.containsKey(key)) {
            return list.get(key).getObject();
        } else {
            return null;
        }
    }

    public <T> int findElementNumber(Map<String, BagElement<T>> list, String key) {
        if (list.containsKey(key)) {
            return list.get(key).getNumber();
        } else {
            return -2;
        }
    }

    public void clearCards() {
        cards.clear();
    }

    public String addItem(Item item) {
        return "";
    }

    public void clearItems() {
        items.clear();
    }

    public String addAmulet(Amulet amulet) {
        return "";
    }

    public void clearAmulet() {
        amulets.clear();
    }

    public Map<String, BagElement<Card>> getCards() {
        return cards;
    }

    public void setCards(Map<String, BagElement<Card>> cards) {
        this.cards = cards;
    }

    public Map<String, BagElement<Item>> getItems() {
        return items;
    }

    public void setItems(Map<String, BagElement<Item>> items) {
        this.items = items;
    }

    public Map<String, BagElement<Amulet>> getAmulets() {
        return amulets;
    }

    public void setAmulets(Map<String, BagElement<Amulet>> amulets) {
        this.amulets = amulets;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
