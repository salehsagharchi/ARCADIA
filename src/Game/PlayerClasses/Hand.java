package Game.PlayerClasses;

import Game.Assets.Card;
import Game.Assets.MonsterCard;
import Game.Assets.SpellCard;
import Game.Enums.CardType;
import Game.Enums.SpellType;
import Game.Exceptions.DeadPlayerException;
import MenuControllers.GeneralClasses.Menus;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Hand implements Field, Serializable {

    private static int limit = 5;
    private Map<Integer, Card> slots;
    private Player player;


    Hand(Player player) {
        this.player = player;

        slots = new LinkedHashMap<>();
        for (int i = 1; i <= limit; i++) {
            slots.put(i, null);
        }

    }

    public String drawCardTo(Player player, int fromInt, int toInt) {
        return drawCardTo(player, String.valueOf(fromInt), String.valueOf(toInt));
    }

    public String drawCardTo(Player player, String fromStr, String toStr) {
        try {
            if (Integer.parseInt(fromStr) <= 0 || Integer.parseInt(fromStr) > limit) {
                return "ERROR : Number1 is not Valid";
            }
        } catch (Exception e) {
            return "ERROR : Number1 Cannot Parse";
        }
        try {
            if (Integer.parseInt(toStr) <= 0) {
                return "ERROR : Number2 is not Valid";
            }
        } catch (Exception e) {
            return "ERROR : Number2 Cannot Parse";
        }

        int handIndx = Integer.parseInt(fromStr);
        int slotIndx = Integer.parseInt(toStr);


        if (getSlotCard(handIndx) == null) {
            return "ERROR : Your Hand Index Is Empty";
        }

        Card ref = getSlotCard(handIndx);

        String res;

        if (getSlotCard(handIndx).getType() == CardType.Spell) {

            if (slotIndx > player.getBag().getSpellField().getLimit()) {
                return "ERROR : Number2 is not Valid";
            }

            if (((SpellCard) ref).getSpellType() != SpellType.Instant) {
                if (player.getBag().getSpellField().getSlotCard(slotIndx) != null) {
                    return "ERROR : Slot " + slotIndx + " Of Your SpellField isn't Empty!";
                }
            }

            if (player.getMP() < ref.getCostMP()) {
                return "ERROR : Your MP is Not Enough! - " + "Cost: " + ref.getCostMP() + " - YourMP: " + player.getMP();
            }

            player.subtractMP(ref.getCostMP());

            boolean seted = false;
            try {
                seted = player.getBag().getSpellField().addCardToSlot(((SpellCard) ref).getSpellType() == SpellType.Instant,
                        ((SpellCard) ref).clone(), slotIndx, true);
            } catch (DeadPlayerException e) {
                return "";
            }

            if (seted) {
                res = "\"" + ref.getName() + "\"" + " was set in SpellField slot " + slotIndx + ". " + ref.getCostMP() + " MP was used.";
            } else {
                res = "";
            }

        } else {

            if (slotIndx > player.getBag().getMonsterField().getLimit()) {
                return "ERROR : Number2 is not Valid";
            }
            if (player.getBag().getMonsterField().getSlotCard(slotIndx) != null) {
                return "ERROR : Slot " + slotIndx + " Of Your MonsterField isn't Empty!";
            }

            if (player.getMP() < ref.getCostMP()) {
                return "ERROR : Your MP is Not Enough! - " + "Cost: " + ref.getCostMP() + " - YourMP: " + player.getMP();
            }

            player.subtractMP(ref.getCostMP());

            try {
                player.getBag().getMonsterField().addCardToSlot(((MonsterCard) ref).clone(), slotIndx, true);
            } catch (DeadPlayerException e) {
                return "";
            }

            res = "OK. \"" + ref.getName() + "\"" + " was set in MonsterField slot " + slotIndx + ". " + ref.getCostMP() + " MP was used.";

        }

        addCardToSlot(null, handIndx);
        ref = null;
        try {
            player.getBag().getSpellField().doAuraSpells();
        } catch (DeadPlayerException e) {

        }

        return res;

    }

    public int findCardSlotByReference(Card card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == card) {
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

                if (temp.getCostMP() > player.getMP()) {
                    res.append(Menus.RED);
                } else {
                    res.append(Menus.CYAN);
                }
                res.append(" [Cost: ").append(temp.getCostMP()).append("]").append(Menus.RESET);
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

    public int findFirstEmptySlotIndex() {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public String addCard(Card card) {
        for (int i = 1; i <= limit; i++) {
            if (slots.get(i) == null) {
                slots.put(i, card);
                return "ok";
            }
        }
        return "hand full";
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
