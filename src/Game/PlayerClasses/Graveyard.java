package Game.PlayerClasses;

import Game.Assets.Card;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Graveyard implements Field, Serializable {

    private ArrayList<Card> cards;

    Graveyard() {
        cards = new ArrayList<>();

    }

    public String showList() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            res.append(i + 1).append(".\t").append(cards.get(i).getName()).append("\n");
        }
        if (res.toString().trim().equals("")) {
            return Menus.RED + "\t" + "--> Not Any Item Now!" + Menus.RESET + "\n";
        }
        return res.toString();
    }

    public String getCardAndDeleteByName(String name) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equalsIgnoreCase(name)) {
                String res = cards.get(i).getName();
                cards.remove(i);
                return res;
            }
        }
        return "#NULL#";
    }


    public void addCard(Card card) {
        cards.add(card);
    }

    @Override
    public int getLimit() {
        return cards.size();
    }

    @Override
    public Card getSlotCard(int index) {
        if (index >= 1 && index <= cards.size()) {
            return cards.get(index - 1);
        } else {
            return null;
        }
    }

    public void emptyGraveyard() {
        cards = new ArrayList<>();
    }
}
