package Game.States;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import Game.Elements.BagElement;
import Game.PlayerClasses.Deck;
import Game.PlayerClasses.Inventory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class State implements Serializable {
    // <slotNumber, CardName>
    private Map<Integer, String> slots;

    // <ElementName, numbers>
    private Map<String, Integer> cardsInven;
    private Map<String, Integer> itemsInven;
    private Map<String, Integer> amuletsInven;


    private int startingBattle;
    private int gil;

    public State() {
        slots = new LinkedHashMap<>();
        cardsInven = new LinkedHashMap<>();
        itemsInven = new LinkedHashMap<>();
        amuletsInven = new LinkedHashMap<>();
    }

    public void showlist() {
        for (Map.Entry<Integer, String> entry :
                slots.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void setSlots(Deck deck) {
        for (int i = 1; i <= deck.getLimit(); i++) {
            if (deck.getSlotCard(i) == null) {
                slots.put(i, "#NULL#");
            } else {
                slots.put(i, deck.getSlotCard(i).getName());
            }
        }
    }

    public void setInventory(Inventory inventory) {
        Map<String, BagElement<Card>> tempcards = inventory.getCards();
        for (Map.Entry<String, BagElement<Card>> entry : tempcards.entrySet()) {
            cardsInven.put(entry.getKey(), entry.getValue().getNumber());
        }

        Map<String, BagElement<Item>> tempitems = inventory.getItems();
        for (Map.Entry<String, BagElement<Item>> entry : tempitems.entrySet()) {
            itemsInven.put(entry.getKey(), entry.getValue().getNumber());
        }

        Map<String, BagElement<Amulet>> tempamulets = inventory.getAmulets();
        for (Map.Entry<String, BagElement<Amulet>> entry : tempamulets.entrySet()) {
            amuletsInven.put(entry.getKey(), entry.getValue().getNumber());
        }
    }

    public Map<Integer, String> getSlots() {
        return slots;
    }

    public Map<String, Integer> getCardsInven() {
        return cardsInven;
    }

    public Map<String, Integer> getItemsInven() {
        return itemsInven;
    }

    public Map<String, Integer> getAmuletsInven() {
        return amuletsInven;
    }

    public int getStartingBattle() {
        return startingBattle;
    }

    public void setStartingBattle(int startingBattle) {
        this.startingBattle = startingBattle;
    }

    public int getGil() {
        return gil;
    }

    public void setGil(int gil) {
        this.gil = gil;
    }
}
