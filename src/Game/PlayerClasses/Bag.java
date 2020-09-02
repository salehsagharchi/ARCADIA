package Game.PlayerClasses;

import Game.Assets.Amulet;
import Game.Enums.PlayerType;

import java.io.Serializable;

public class Bag implements Serializable {
    private Amulet amulet;
    private Player player;

    private Hand hand;
    private Deck deck;
    private Graveyard graveyard;
    private MonsterField monsterField;
    private SpellField spellField;

    public Bag(Player player) {
        this.player = player;
        deck = new Deck(this.player.getType() == PlayerType.Human);
        hand = new Hand(player);
        graveyard = new Graveyard();
        spellField = new SpellField(player);
        monsterField = new MonsterField(player);

    }

    public String equippedAmulet() {
        if (amulet == null) {
            return "NULL!";
        } else {
            return amulet.getName();
        }
    }


    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Graveyard getGraveyard() {
        return graveyard;
    }

    public void setGraveyard(Graveyard grave) {
        this.graveyard = grave;
    }

    public MonsterField getMonsterField() {
        return monsterField;
    }

    public void setMonsterField(MonsterField monsterField) {
        this.monsterField = monsterField;
    }

    public SpellField getSpellField() {
        return spellField;
    }

    public void setSpellField(SpellField spellField) {
        this.spellField = spellField;
    }

    public Amulet getAmulet() {
        return amulet;
    }

    public void setAmulet(Amulet amulet) {
        this.amulet = amulet;
    }

    public boolean isEmptyAll() {
        if (getHand().emptySlots() == getHand().getLimit() &&
                getMonsterField().emptySlots() == getMonsterField().getLimit() &&
                getDeck().getFullSlots() == 0) {
            return true;
        }
        return false;
    }
}
