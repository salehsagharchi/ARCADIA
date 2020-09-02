package Game.PlayerClasses;

import Game.Assets.Card;
import Game.Constants.Initializer_Statics;
import Game.Exceptions.DeckIsEmptyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.*;

public class Deck implements Serializable {

    private static int limit = 30;
    private Map<Integer, Card> slots;

    // For Network Game
    private String networkBattleKey = "";

    public Deck(boolean defaultDeck) {
        slots = new LinkedHashMap<>();
        for (int i = 1; i <= limit; i++) {
            slots.put(i, null);
        }
        if (defaultDeck) {
            ArrayList<Card> tempCards = Initializer_Statics.requestDefaultDeck();
            int index = 1;
            for (Card card : tempCards) {
                slots.put(index, card);
                index++;
                if (index > limit) {
                    break;
                }
            }
        }

    }

    public String getOneCardAndDelete(String key) throws DeckIsEmptyException {
        for (int i = 1; i <= limit; i++) {
            if (getSlotCard(i) != null) {
                if (key.equals(getSlotName(i))) {
                    String res = getSlotName(i);
                    addCardToSlot(null, i);
                    return res;
                }
            }
        }
        return null;
    }


    public String getOneCardAndDelete() throws DeckIsEmptyException {
        for (int i = 1; i <= limit; i++) {
            if (getSlotCard(i) != null) {
                String res = getSlotName(i);
                addCardToSlot(null, i);
                return res;
            }
        }
        throw new DeckIsEmptyException("End Of Deck!");
    }

    public int getLimit() {
        return limit;
    }

    public ObservableList<String> getList() {
        ObservableList<String> items = FXCollections.observableArrayList();
        String res = "";
        for (int i = 1; i <= limit; i++) {
            if (getSlotCard(i) == null) {
                res = "RED;" + ("Slot ") + (i < 10 ? "0" : "") + (i) + ":\t" + getSlotName(i);
            } else {
                res = "GREEN;" + ("Slot ") + (i < 10 ? "0" : "") + (i) + ":\t\"" + getSlotName(i) + "\"";
            }
            items.add(res);
        }
        return items;
    }

    public String showList() {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= limit; i++) {
            res.append("Slot ").append(i < 10 ? "0" : "").append(i).append(":\t").append(getSlotName(i)).append("\n");
        }
        return res.toString();
    }

    public Card getSlotCard(int index) {
        return slots.getOrDefault(index, null);
    }

    public String getSlotName(int index) {
        if (index >= 1 && index <= limit) {
            if (slots.get(index) == null) {
                return "#EMPTY#";
            }
            return slots.get(index).getName();
        }
        return "index out of range";
    }

    public String addCard(Card card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                slots.put(i, card);
                return "ok";
            }
        }
        return "deck full";
    }

    public String addCardToSlot(Card card, int slot) {
        slots.put(slot, card);
        return "ok";
    }

    public String removeCard(int index) {
        if (index >= 1 && index <= limit) {
            slots.put(index, null);
            return "ok";
        }
        return "index out of range";
    }


    public int findCardNumbers(String key) {
        int res = 0;
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                if (slots.get(i).getName().equals(key)) {
                    res++;
                }
            }
        }
        return res;
    }

    public void Shuffle() {
        ArrayList<Card> tempCards = new ArrayList<>();
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                tempCards.add(slots.get(i));
            }
        }
        for (int i = 1; i <= limit; i++) {
            slots.put(i, null);
        }
        Random random = new Random();
        int newIndex = random.nextInt(30) + 1;
        for (Card card : tempCards) {
            while (slots.get(newIndex) != null) {
                newIndex = random.nextInt(30) + 1;
            }
            slots.put(newIndex, card);
        }
    }

    public int getFullSlots() {
        int res = 0;
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                res++;
            }
        }
        return res;
    }

    public String getNetworkBattleKey() {
        return networkBattleKey;
    }

    public void setNetworkBattleKey(String networkBattleKey) {
        this.networkBattleKey = networkBattleKey;
    }
}
