package Game.Shops;

import Game.Assets.Card;
import Game.Assets.MonsterCard;
import Game.Elements.ShopElement;
import Game.Enums.CardType;
import Game.Enums.MonsterTribe;
import Game.Enums.MonsterType;
import Game.GameManager;
import Game.PlayerClasses.Player;
import MenuControllers.GeneralClasses.Menus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.*;

public class CardShop implements Serializable {
    private Map<String, ShopElement<Card>> cards;

    private static List<String> cannotBuySpell = Arrays.asList("MeteorShower", "Reaper'sScythe", "BloodFeast");
    private static List<MonsterTribe> canBuyMonster = Arrays.asList(MonsterTribe.Atlantian, MonsterTribe.DragonBreed, MonsterTribe.Elven);

    public CardShop() {
        cards = new LinkedHashMap<>();
    }

    public Map<String, ShopElement<Card>> getCards() {
        return cards;
    }

    public void setCards(Map<String, ShopElement<Card>> cards) {
        this.cards = cards;
    }

    public int getMaxCardName() {
        int res = 0;
        for (Map.Entry<String, ShopElement<Card>> entry :
                cards.entrySet()) {
            Card myEntry = entry.getValue().getObject();
            if (myEntry.getName().length() > res) {
                res = myEntry.getName().length();
            }
        }
        return res;
    }

    public ObservableList<String> getCardList(boolean showAll) {
        ObservableList<String> items = FXCollections.observableArrayList();
        String res = "";
        int nSpace = getMaxCardName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Card>> entry :
                cards.entrySet()) {

            Card myEntry = entry.getValue().getObject();
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
        int nSpace = getMaxCardName();
        int index = 1;
        for (Map.Entry<String, ShopElement<Card>> entry :
                cards.entrySet()) {


            Card myEntry = entry.getValue().getObject();
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

    public String addCard(Card card) {

        if (card.getType() == CardType.Monster) {
            if (canBuyMonster.contains(((MonsterCard) card).getTribe())) {
                // can buy
                MonsterType type = ((MonsterCard) card).getMonsterType();

                if (type == MonsterType.Normal) {
                    cards.put(card.getName(), new ShopElement<>(card, 4, 4));
                } else if (type == MonsterType.SpellCaster) {
                    cards.put(card.getName(), new ShopElement<>(card, 2, 2));
                } else if (type == MonsterType.General) {
                    cards.put(card.getName(), new ShopElement<>(card, 2, 2));
                } else {
                    // Hero
                    cards.put(card.getName(), new ShopElement<>(card, 1, 1));
                }
            } else {
                // cannot buy
                cards.put(card.getName(), new ShopElement<>(card, 0, 0));
            }
        } else {
            if (!cannotBuySpell.contains(card.getName())) {
                // can buy
                if (card.getCostMP() < 6) {
                    cards.put(card.getName(), new ShopElement<>(card, 3, 3));
                } else {
                    cards.put(card.getName(), new ShopElement<>(card, 2, 2));
                }
            } else {
                // cannot buy
                cards.put(card.getName(), new ShopElement<>(card, 0, 0));
            }
        }


        return "OK";
    }


    public String buyCard(String nameString, String numString, GameManager manager, Player player) {
        if (findCard(nameString) == null) {
            return "ERROR : Card Name";
        }
        ShopElement<Card> cardElement = findCardElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);
        if (cardElement.getInitValue() == 0) {
            return "You Cannot Buy \"" + cardElement.getObject().getName() + "\" Card!";
        }
        if (cardElement.getRemain() - num < 0) {
            return "Remain is not Enough !";
        }
        if (player.getGil() < cardElement.getObject().getPrice() * num) {
            return "Not Enough Gil!";
        }
        cardElement.subtractRemain(num);
        player.getInventory().addCard(cardElement.getObject().getName(), num);
        player.subtractGil(cardElement.getObject().getPrice() * num);
        return "Successfully Bought " + num + " of \"" + cardElement.getObject().getName() + "\"!";
    }

    public String sellCard(String nameString, String numString, GameManager manager, Player player) {
        if (player.getInventory().findElementNumber(player.getInventory().getCards(), nameString) == -2) {
            return "ERROR : Card Name";
        }
        ShopElement<Card> cardElement = findCardElement(nameString);
        try {
            if (Integer.parseInt(numString) <= 0) {
                return "ERROR : Number is not Valid";
            }
        } catch (Exception e) {
            return "Error : Number Cannot Parse";
        }
        int num = Integer.parseInt(numString);

        if (player.getInventory().findElementNumber(player.getInventory().getCards(), nameString) - num < 0) {
            return "Not Enough Card!";
        }

        if (player.getInventory().findElementNumber(player.getInventory().getCards(), nameString) -
                player.getBag().getDeck().findCardNumbers(cardElement.getObject().getName()) < num) {
            return "No Cannot Sell Your Deck Card!";
        }

        if (cardElement.getInitValue() == 0) {
            return "You Cannot Sell \"" + cardElement.getObject().getName() + "\" Card!";
        }
        cardElement.addRemain(num);
        player.getInventory().removeCard(cardElement.getObject().getName(), num);
        player.addGil(cardElement.getObject().getPrice() * num / 2);
        return "Successfully Sold " + num + " of \"" + cardElement.getObject().getName() + "\"!";
    }


    public ShopElement<Card> findCardElement(String key) {
        return cards.getOrDefault(key, null);
    }

    public Card findCard(String key) {
/*        ArrayList<Card> possibles = new ArrayList<>();
        for (Map.Entry<String, ShopElement<Card>> entry : cards.entrySet()) {
            if (entry.getKey().toLowerCase().contains(key.toLowerCase())) {
                possibles.add(entry.getValue().getObject());
            }
        }
        if (possibles.size() == 1) {
            return possibles.get(0);
        }
        return null;*/
        if (cards.containsKey(key)) {
            return cards.get(key).getObject();
        } else {
            return null;
        }
    }

    public int findCardInitValue(String key) {
        if (cards.containsKey(key)) {
            return cards.get(key).getInitValue();
        } else {
            return -2;
        }
    }

    public int findCardRemain(String key) {
        if (cards.containsKey(key)) {
            return cards.get(key).getRemain();
        } else {
            return -2;
        }
    }

    public void clearCards() {
        cards.clear();
    }
}
