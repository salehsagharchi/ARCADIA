package Game.PlayerClasses;

import Game.Assets.Card;
import Game.Assets.SpellCard;
import Game.Enums.SpellType;
import Game.Exceptions.DeadPlayerException;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpellField implements Field, Serializable {

    private static int limit = 3;
    private Map<Integer, SpellCard> slots;
    private Player player;


    SpellField(Player player) {
        this.player = player;
        slots = new LinkedHashMap<>();
        for (int i = 1; i <= limit; i++) {
            slots.put(i, null);
        }

    }

    // boolean = set to field or not
    public boolean addCardToSlot(boolean onlyUseSpell, SpellCard card, int slot, boolean drawFuncs) throws DeadPlayerException {
        if (onlyUseSpell && card.getSpellType() == SpellType.Instant) {
            card.executeMagicCommand(player);
            player.getBag().getGraveyard().addCard(card.clone());
            return false;
        }
        slots.put(slot, card);
        if (drawFuncs) {
            if (card.getSpellType() != SpellType.Instant) {
                card.executeMagicCommand(player);
                return true;
            }
        }
        return true;
    }

    public void doContinuousSpells() throws DeadPlayerException {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                if (slots.get(i).getSpellType() == SpellType.Continuous) {
                    slots.get(i).executeMagicCommand(player);
                }
            }
        }
    }

    public void doAuraSpells() throws DeadPlayerException {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) != null) {
                if (slots.get(i).getSpellType() == SpellType.Aura) {
                    slots.get(i).executeMagicCommand(player);
                }
            }
        }
    }

    public int findCardSlotByReference(SpellCard card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == card) {
                return i;
            }
        }
        return -1;
    }

    public int findFirstEmptySlotIndex() {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public String showList() {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= limit; i++) {
            res.append("Slot ").append(i).append(":\t");
            Card temp = getSlotCard(i);
            if (temp != null) {
                res.append(Menus.GREEN).append(getSlotName(i)).append(Menus.RESET);
                res.append(" ").append(temp.getSummaryInfo());
            } else {
                res.append(Menus.RED).append(getSlotName(i)).append(Menus.RESET);
            }
            res.append("\n");
        }
        return res.toString();
    }

    public int getLimit() {
        return limit;
    }

    public SpellCard getSlotCard(int index) {
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

    public String addCard(SpellCard card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                slots.put(i, card);
                return "ok";
            }
        }
        return "sfield full";
    }

    public String removeCard(int index) {
        if (index >= 1 && index <= limit) {
            slots.put(index, null);
            return "ok";
        }
        return "index out of range";
    }


    public int emptySlots() {
        int res = 0;
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                res++;
            }
        }
        return res;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
