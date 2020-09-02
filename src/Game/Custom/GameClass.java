package Game.Custom;

import Game.Assets.Amulet;
import Game.Assets.Card;
import Game.Assets.Item;
import Game.PlayerClasses.Deck;
import Game.PlayerClasses.Inventory;
import Game.PlayerClasses.Player;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameClass implements Serializable {
    private String gameName;
    private Map<String, Card> cardMap;
    private Map<String, Item> itemMap;
    private Map<String, Amulet> amuletMap;
    private Player player;
    private CardShop cardShop;
    private ItemShop itemShop;
    private AmuletShop amuletShop;

    public GameClass(String gameName) {
        this.gameName = gameName.trim();
        cardMap = new LinkedHashMap<>();
        itemMap = new LinkedHashMap<>();
        amuletMap = new LinkedHashMap<>();
    }

    public void addCard(Card card) {
        cardMap.put(card.getName(), card.clone());
    }

    public void addItem(Item item) {
        itemMap.put(item.getName(), item.clone());
    }

    public void addAmulet(Amulet amulet) {
        amuletMap.put(amulet.getName(), amulet.clone());
    }

    public String getFileName() {
        return GameClass.getFileName(getGameName());
    }

    public static String getFileName(String gameName) {
        String res = gameName;
        while (res.contains("  ")) {
            res = res.replace("  ", " ");
        }
        return res.toLowerCase().replaceAll(" ", "_") + ".game";
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName.trim();
    }

    public Map<String, Card> getCardMap() {
        return cardMap;
    }

    public void setCardMap(Map<String, Card> cardMap) {
        this.cardMap = cardMap;
    }

    public Map<String, Item> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, Item> itemMap) {
        this.itemMap = itemMap;
    }

    public Map<String, Amulet> getAmuletMap() {
        return amuletMap;
    }

    public void setAmuletMap(Map<String, Amulet> amuletMap) {
        this.amuletMap = amuletMap;
    }

    public CardShop getCardShop() {
        return cardShop;
    }

    public void setCardShop(CardShop cardShop) {
        this.cardShop = cardShop;
    }

    public ItemShop getItemShop() {
        return itemShop;
    }

    public void setItemShop(ItemShop itemShop) {
        this.itemShop = itemShop;
    }

    public AmuletShop getAmuletShop() {
        return amuletShop;
    }

    public void setAmuletShop(AmuletShop amuletShop) {
        this.amuletShop = amuletShop;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        player.setPlayingGameClass(this);
    }

    @Override
    public String toString() {
        return gameName;
    }
}
