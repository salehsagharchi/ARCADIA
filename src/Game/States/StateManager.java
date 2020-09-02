package Game.States;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import Game.Constants.Initializer_Statics;
import Game.Elements.BagElement;
import Game.Enums.BattleState;
import Game.GameManager;
import Game.PlayerClasses.Deck;
import Game.PlayerClasses.Inventory;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StateManager {

    public static void saveState(String fileName, Deck deck, Inventory inventory, GameManager gameManager) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            State state = new State();

            state.setGil(gameManager.getHumanPlayer().getGil());
            state.setSlots(deck);
            state.setInventory(inventory);
            state.setStartingBattle(gameManager.getStartingBattle());

            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(state);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    public static State restoreState(String fileName, Deck deck, Inventory inventory, GameManager gameManager, boolean subtractMysticH) {
        State state = null;

        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            state = (State) in.readObject();
            in.close();
            fileIn.close();

            // Set Deck
            for (int i = 1; i <= deck.getLimit(); i++) {
                deck.addCardToSlot(null, i);
            }
            for (Map.Entry<Integer, String> entry : state.getSlots().entrySet()) {
                if (entry.getValue().equalsIgnoreCase("#NULL#")) {
                    continue;
                }
                deck.addCardToSlot(Initializer_Statics.getCloneOfCard(entry.getValue()), entry.getKey());
            }

            // Set Inventory
            Map<String, BagElement<Card>> cardsInven = new LinkedHashMap<>();
            Map<String, BagElement<Item>> itemsInven = new LinkedHashMap<>();
            Map<String, BagElement<Amulet>> amuletsInven = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : state.getCardsInven().entrySet()) {
                cardsInven.put(entry.getKey(), new BagElement<>(Initializer_Statics.getCloneOfCard(entry.getKey()), entry.getValue()));
            }
            for (Map.Entry<String, Integer> entry : state.getItemsInven().entrySet()) {
                if (entry.getKey().startsWith("Mystic")) {
                    if (subtractMysticH) {
                        if (entry.getValue() > 1) {
                            itemsInven.put(entry.getKey(), new BagElement<>(Initializer_Statics.getCloneOfItem(entry.getKey()), entry.getValue() - 1));
                        }
                    } else {
                        itemsInven.put(entry.getKey(), new BagElement<>(Initializer_Statics.getCloneOfItem(entry.getKey()), entry.getValue()));
                    }
                } else {
                    itemsInven.put(entry.getKey(), new BagElement<>(Initializer_Statics.getCloneOfItem(entry.getKey()), entry.getValue()));
                }
            }
            for (Map.Entry<String, Integer> entry : state.getAmuletsInven().entrySet()) {
                amuletsInven.put(entry.getKey(), new BagElement<>(Initializer_Statics.getCloneOfAmulet(entry.getKey()), entry.getValue()));
            }
            inventory.setCards(cardsInven);
            inventory.setAmulets(amuletsInven);
            inventory.setItems(itemsInven);

            // Set Gil Last Battle

            gameManager.getHumanPlayer().setGil(state.getGil());

            if (state.getStartingBattle() == -1) {
                for (int i = 0; i <= 3; i++) {
                    gameManager.getBattle(i).setState(BattleState.Ended);
                }
            } else {
                for (int i = 0; i < state.getStartingBattle(); i++) {
                    gameManager.getBattle(i).setState(BattleState.Ended);
                }
                for (int i = state.getStartingBattle(); i <= 3; i++) {
                    gameManager.getBattle(i).setState(BattleState.NotStarted);
                }
            }


        } catch (IOException i) {
            i.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
        }

        return state;
    }
}
