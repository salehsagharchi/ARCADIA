package Game.Constants;

import Game.Assets.*;
import Game.Elements.BagElement;
import Game.Enums.CardType;
import Game.Enums.MonsterTribe;
import Game.Enums.MonsterType;
import Game.Enums.SpellType;
import Game.Shops.AmuletShop;
import Game.Shops.CardShop;
import Game.Shops.ItemShop;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class Initializer_Statics {

    private static Map<String, Card> cardList;
    private static Map<String, Item> itemList;
    private static Map<String, Amulet> amuletList;

    private static ArrayList<String> defaultHumanCards;
    private static ArrayList<String> defaultHumanDeckCards;
    private static ArrayList<String> defaultHumanItems;
    private static ArrayList<String> defaultHumanAmulets;

    static {
        initializeAllStaticItems();

        defaultHumanAmulets = getConstantsFromFile("defaultHumanAmulets");
        defaultHumanCards = getConstantsFromFile("defaultHumanCards");
        defaultHumanDeckCards = getConstantsFromFile("defaultHumanDeckCards");
        defaultHumanItems = getConstantsFromFile("defaultHumanItems");


    }

    public static ArrayList<String> getConstantsFromFile(String code) {
        String out = "";
        try {
            out = IOUtils.toString(
                    Initializer_Statics.class.getResourceAsStream("Constants.txt"),
                    "UTF-8"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> output = new ArrayList<>();

        String[] lines = out.split("\n");
        boolean find = false;
        for (String line : lines) {
            if (line.trim().equals("")) {
                continue;
            }
            line = line.trim();

            if (line.equals("}") && find) {
                break;
            }
            if (find) {
                output.add(line);
            }
            if (line.equalsIgnoreCase(code.trim() + "{")) {
                find = true;
            }
        }

        return output;
    }


    public static Map<String, BagElement<Card>> requestDefaultCards() {
        //2 - 2 - 1 - 1 - 1
        //First Aid Kit x3
        //Throwing Knives x3
        //Poisonous Cauldron x1
        //Healing Ward x1
        //War Drum x1

        Map<String, BagElement<Card>> cards = new LinkedHashMap<>();

//        //SpellsManager:
//        cards.put("FirstAidKit", new BagElement<>(getCloneOfCard("FirstAidKit"), 3));
//        cards.put("ThrowingKnives", new BagElement<>(getCloneOfCard("ThrowingKnives"), 3));
//        cards.put("PoisonousCauldron", new BagElement<>(getCloneOfCard("PoisonousCauldron"), 1));
//        cards.put("HealingWard", new BagElement<>(getCloneOfCard("HealingWard"), 1));
//        cards.put("WarDrum", new BagElement<>(getCloneOfCard("WarDrum"), 1));

        for (String entry : defaultHumanCards) {
            if (entry.contains("@")) {
                cards.put(entry.split("@")[0], new BagElement<>(getCloneOfCard(entry.split("@")[0]), Integer.parseInt(entry.split("@")[1])));
            }
        }


        return cards;
    }


    public static Map<String, BagElement<Item>> requestDefaultItems() {
        Map<String, BagElement<Item>> items = new LinkedHashMap<>();
        for (String entry : defaultHumanItems) {
            if (entry.contains("@")) {
                items.put(entry.split("@")[0], new BagElement<>(getCloneOfItem(entry.split("@")[0]), Integer.parseInt(entry.split("@")[1])));
            }
        }
        return items;
    }

    public static Map<String, BagElement<Amulet>> requestDefaultAmulets() {
        Map<String, BagElement<Amulet>> amulets = new LinkedHashMap<>();
        for (String entry : defaultHumanAmulets) {
            if (entry.contains("@")) {
                amulets.put(entry.split("@")[0], new BagElement<>(getCloneOfAmulet(entry.split("@")[0]), Integer.parseInt(entry.split("@")[1])));
            }
        }
        return amulets;
    }

    public static ArrayList<Card> requestDefaultDeck() {
        ArrayList<Card> cards = new ArrayList<>();
        for (String entry : defaultHumanDeckCards) {
            if (entry.contains("@")) {
                int num = Integer.parseInt(entry.split("@")[1]);
                for (int i = 0; i < num; i++) {
                    cards.add(getCloneOfCard(entry.split("@")[0]));
                }
            }
        }
        return cards;
    }


    public static Card getCloneOfCard(String key) {
        if (cardList.containsKey(key)) {
            if (cardList.get(key).getType() == CardType.Monster) {
                return ((MonsterCard) cardList.get(key)).clone();
            } else {
                return ((SpellCard) cardList.get(key)).clone();
            }
        } else {
            System.out.println("not found");
            return null;
        }
    }

    public static Item getCloneOfItem(String key) {
        if (itemList.containsKey(key)) {
            return itemList.get(key).clone();
        } else {
            System.out.println("not found");
            return null;
        }
    }

    public static Amulet getCloneOfAmulet(String key) {
        if (amuletList.containsKey(key)) {
            return amuletList.get(key).clone();
        } else {
            System.out.println("not found");
            return null;
        }
    }


    public static void initializeAllShopElements(CardShop cardShop, ItemShop itemShop, AmuletShop amuletShop) {
        for (Map.Entry<String, Card> entry : cardList.entrySet()) {
            if (entry.getValue().getType() == CardType.Monster) {
                cardShop.addCard(((MonsterCard) entry.getValue()).clone());
            } else {
                cardShop.addCard(((SpellCard) entry.getValue()).clone());
            }
        }
        for (Map.Entry<String, Item> entry : itemList.entrySet()) {
            itemShop.addItem(entry.getValue().clone());
        }
        for (Map.Entry<String, Amulet> entry : amuletList.entrySet()) {
            amuletShop.addAmulet(entry.getValue().clone());
        }
    }

    private static void initializeAllStaticItems() {
        cardList = new LinkedHashMap<>();
        itemList = new LinkedHashMap<>();
        amuletList = new LinkedHashMap<>();
        initItems();
        initAmulets();
        initCards();
//        System.out.println(":");
//        System.out.println(itemList.size());
//        System.out.println(cardList.size());
//        System.out.println(amuletList.size());
    }

    private static void initItems() {
        addItemToList(new Item("Small HP Potion", 1000,
                ":+HP(500)", "Increase Player’s HP by 500"));

        addItemToList(new Item("Medium HP Potion", 2000,
                ":+HP(1000)", "Increase Player’s HP by 1000"));

        addItemToList(new Item("Large HP Potion", 4000,
                ":+HP(2000)", "Increase Player’s HP by 2000"));

        addItemToList(new Item("Small MP Potion", 1000,
                ":+MP(2)", "Increase Player’s MP by 2"));

        addItemToList(new Item("Medium MP Potion", 2000,
                ":+MP(4)", "Increase Player’s MP by 4"));

        addItemToList(new Item("Large MP Potion", 4000,
                ":+MP(8)", "Increase Player’s MP by 8"));

        addItemToList(new Item("Lesser Restorative", 2000,
                ":+HP(500):+MP(2)", "Increase Player’s HP by 500 and MP by 2"));

        addItemToList(new Item("Greater Restorative", 4000,
                ":+HP(1000):+MP(4)", "Increase Player’s HP by 1000 and MP by 4"));

        addItemToList(new Item("Mystic Hourglass", -1, ":NOTUSE",
                "Recovery Player State"));

    }

    private static void initAmulets() {

        addAmuletToList(new Amulet("Iron Pendant", 2000,
                ":+MHP(500)", "Increase Player’s Max HP by 500"));

        addAmuletToList(new Amulet("Gold Pendant", 4000,
                ":+MHP(1000)", "Increase Player’s Max HP by 1000"));

        addAmuletToList(new Amulet("Diamond Pendant", 8000,
                ":+MHP(2000)", "Increase Player’s Max HP by 2000"));

        addAmuletToList(new Amulet("Iron Ring", 2000,
                ":+MMP(1)", "Increase Player’s Max MP by 1"));

        addAmuletToList(new Amulet("Gold Ring", 4000,
                ":+MMP(2)", "Increase Player’s Max MP by 2"));

        addAmuletToList(new Amulet("Diamond Ring", 8000,
                ":+MMP(3)", "Increase Player’s Max MP by 3"));

        addAmuletToList(new Amulet("DemonKC", -1,
                ":FUNC1", "Decrease All Incoming Damages by 20%"));

    }

    private static void initCards() {


        initSpellCard();


        initElvenCard();
        initDragonCard();
        initAtlantianCard();
        initGoblinCard();
        initOgreCard();
        initVampiricCard();
        initDemonicCard();

        String res = "";

        for (Map.Entry<String, Card> entry :
                cardList.entrySet()) {
            if (entry.getValue().getType() == CardType.Spell) {
                res += ((SpellCard) entry.getValue()).getMagicCommand().trim() + "\n";
            } else {
                res += ((MonsterCard) entry.getValue()).getMagicCommand().trim() + "\n";
            }

        }

        //System.out.println(res);

    }

    private static void initSpellCard() {

        // SpellsManager:
        addCardToList(new SpellCard("Throwing Knives", 3, SpellType.Instant,
                "Deal 500 damage to a selected enemy monster card on the field or to enemy player",
                ":-HP(500)?emon-eply", ""));

        addCardToList(new SpellCard("Poisonous Cauldron", 4, SpellType.Continuous,
                "Deal 100 damage to all enemy monster cards and enemy player",
                ":-HP(100)=emon-eply", ""));

        addCardToList(new SpellCard("First Aid Kit", 3, SpellType.Instant,
                "Increase HP of a selected friendly monster card or player by 500",
                ":+HP(500)?fmon-fply", ""));

        addCardToList(new SpellCard("Reaper's Scythe", 4, SpellType.Instant,
                "Send an enemy monster or spell card from field to graveyard",
                ":GR?emon-espl", ""));

        addCardToList(new SpellCard("Meteor Shower", 8, SpellType.Continuous,
                "Deal 800 damage to a random enemy monster card on field or player",
                ":-HP(800);emon-eply", ""));

        addCardToList(new SpellCard("Lunar Blessing", 6, SpellType.Aura,
                "Increase AP and HP of friendly Elven monster cards by 300",
                ":+HP(300)=f(Elven):+AP(300)=f(Elven)", ""));

        addCardToList(new SpellCard("Strategic Retreat", 6, SpellType.Instant,
                "Select and move a monster card from field to hand and draw one card from deck",
                ":HND?fmon:DHND", ""));

        addCardToList(new SpellCard("War Drum", 6, SpellType.Aura,
                "Increase all friendly monster cards’ AP by 300",
                ":+AP(300)=fmon", ""));

        addCardToList(new SpellCard("Healing Ward", 5, SpellType.Continuous,
                "Increase all friendly monster cards’ HP by 200",
                ":+HP(200)=fmon", ""));

        addCardToList(new SpellCard("Blood Feast", 4, SpellType.Instant,
                "Deal 500 damage to enemy player and heal your player for 500 HP",
                ":-HP(500)=eply:+HP(500)=fply", ""));

        addCardToList(new SpellCard("Tsunami", 6, SpellType.Instant,
                "Deal 500 damage to all non-Atlantian monster cards on both sides of field",
                //Elven, Atlantian, DragonBreed, Goblin, Ogre, Vampiric, Demonic
                ":-HP(500)=f(Elven)-f(DragonBreed)-f(Goblin)-f(Ogre)-f(Vampiric)-f(Demonic)-e(Elven)-e(DragonBreed)-e(Goblin)-e(Ogre)-e(Vampiric)-e(Demonic)", ""));

        addCardToList(new SpellCard("Take All You Can", 7, SpellType.Aura,
                "Increase all friendly normal monster cards’ HP and AP by 400",
                ":+HP(400)=f(Normal):+AP(400)=f(Normal)", ""));

        addCardToList(new SpellCard("Arcane Bolt", 5, SpellType.Instant,
                "Deal 500 damage to enemy player and select and move an enemy spell card from field to graveyard",
                ":-HP(500)=eply:GR?espl", ""));

        addCardToList(new SpellCard("Greater Purge", 7, SpellType.Instant,
                "Remove all spell cards on field from both sides and move them to hand",
                ":HND=espl-fspl", ""));

        addCardToList(new SpellCard("Magic Seal", 9, SpellType.Continuous,
                "Remove all enemy spell cards from field and move them to graveyard",
                ":GR=espl", ""));
    }

    private static void initElvenCard() {

        // Elven Monster:
        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Ranger", 300, 400, 1, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Hunter", 800, 600, 3, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Guardsman", 1500, 500, 5, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Assassin", 800, 1200, 5, MonsterType.Normal, false, true
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Druid", 1200, 600, 5, MonsterType.SpellCaster, false, false
                , "", "Rejuvenation: Increase a selected friendly monster card’s HP by 500 and AP by 300", ""
                , "*:+HP(500)?fmon:+AP(300)?fmon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Elven Sorceress", 1000, 1000, 7, MonsterType.SpellCaster, false, true
                , "", "Arcane Explosion: Deal 400 damage to all enemy monster cards and remove a random spell card from enemy field and move it to graveyard.", ""
                , "*:-HP(400)=emon:GR;espl*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Noble Elf", 2000, 2400, 9, MonsterType.General, false, false
                , "Purge: Remove all enemy spell cards on the field and move them to hand", "", "Increase a random friendly Elven monster card on the field’s HP by 800 and AP by 600"
                //Increase a random friendly Elven monster card on the field’s HP by 800 and AP by 600
                , ":HND=espl**:+HP(800);f(Elven):+AP(600);f(Elven)", ""));

        addCardToList(new MonsterCard(MonsterTribe.Elven, "Luthien, The High Priestess", 2500, 2000, 9, MonsterType.Hero, false, false
                , "Revive Allies: move two random cards from your graveyard to hand", "Divine Blessing: Increase HP of a friendly monster card or player by 2500",
                "Burst of Light: Increase HP of all friendly monster cards and player by 500 and increase AP of all friendly monster cards by 200"
                , ":GHND;fmon-fspl:GHND;fmon-fspl*:+HP(2500)?fmon-fply*:+HP(500)=fmon-fply:+AP(200)=fmon", ""));
    }

    private static void initDragonCard() {


        // DragonBreed Monster:
        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Lesser Whelp", 500, 300, 1, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Dragonling", 700, 700, 3, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Armored Dragon", 2000, 400, 5, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Yellow Drake", 800, 100, 5, MonsterType.Normal, false, true
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Blue Dragon", 800, 1200, 5, MonsterType.SpellCaster, false, false
                , "", "Magical Fire: Move an enemy monster card from field to graveyard", ""
                , "*:GR?emon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Volcanic Dragon", 2500, 700, 8, MonsterType.SpellCaster, true, false
                , "", "Lava Spit: Deal 500 damage to an enemy monster card and reduce its AP by 500", ""
                , "*:-HP(500)?emon:-AP(500)?emon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "Greater Dragon", 2000, 1800, 8, MonsterType.General, false, false
                , "Devour: Send a random enemy monster card from field to graveyard", "", "Dragon’s Call: draw two cards from deck to hand"
                , ":GR;emon**:DHND:DHND", ""));

        addCardToList(new MonsterCard(MonsterTribe.DragonBreed, "IgneelTDK", 4000, 800, 10, MonsterType.Hero, false, false
                , "King’s Grace: Send all non-Hero monster cards on both sides of field to their graveyards",
                "King’s Wing Slash: Deal 600 damage to all enemy monster cards and player", "King’s Wail: Decrease all enemy monster cards’ AP by 400"
                , ":GR=f(Normal)-f(SpellCaster)-f(General)-e(Normal)-e(SpellCaster)-e(General)*:-HP(600)=emon-eply*:-AP(400)=emon", ""));
    }

    private static void initAtlantianCard() {


        // Atlantian Monster:
        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Murloc Crawler", 200, 500, 1, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Murloc Warrior", 600, 600, 2, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Giant Crab", 1500, 300, 4, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Shark Man", 900, 1200, 5, MonsterType.Normal, false, true
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Naga Siren", 1200, 600, 6, MonsterType.SpellCaster, false, false
                , "", "Song of the Siren: Increase HP of all friendly monster cards by 300 and their AP by 200", ""
                , "*:+HP(300)=fmon:+AP(200)=fmon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Sea Serpent", 1500, 1200, 7, MonsterType.SpellCaster, false, true
                , "", "Serpent’s Bite: Deal 1000 damage to an enemy monster card or player", ""
                , "*:-HP(1000)?emon-eply*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "Kraken", 1800, 2200, 8, MonsterType.General, false, false
                , "Titan’s Presence: Return one random enemy monster card from field to hand and reduce all enemy monsters’ AP by 200", "",
                "Titan’s Fall: Deal 400 damage to all enemy monster cards and player"
                , ":HND;emon:-AP(200)=emon**:-HP(400)=emon-eply", ""));

        addCardToList(new MonsterCard(MonsterTribe.Atlantian, "NeptuneKOA", 2200, 2500, 10, MonsterType.Hero, false, false
                , "Call to Arms: Select and move a card from hand to play field",
                "Trident Beam: Deal 800 damage to three random enemy monster cards or player",
                "Ocean’s Cry: Deal 400 damage to all enemy monster cards and player and increase all friendly Atlantian monster cards’ AP by 500"
                , ":HDFL?fmon-fspl*:-HP(800);emon-eply:-HP(800);emon-eply:-HP(800);emon-eply*:-HP(400)=emon-eply:+AP(500)=f(Atlantian)", ""));
    }

    private static void initGoblinCard() {


        // Goblin Monster:
        addCardToList(new MonsterCard(MonsterTribe.Goblin, "Goblin Smuggler", 600, 400, 2, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Goblin, "Goblin Shaman", 1000, 700, 4, MonsterType.SpellCaster, false, false
                , "", "Mend: Increase a friendly monster card or player’s HP by 400", ""
                , "*:+HP(400)?fmon-fply*", ""));
    }

    private static void initOgreCard() {


        // Orge Monster:
        addCardToList(new MonsterCard(MonsterTribe.Ogre, "Ogre Warrior", 800, 500, 3, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Ogre, "Ogre Frontliner", 1800, 600, 5, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Ogre, "Ogre Magi", 1500, 800, 5, MonsterType.SpellCaster, false, false
                , "", "Enrage: Increase a friendly monster card’s AP by 400", ""
                , "*:+AP(400)?fmon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Ogre, "Ogre Warchief", 2500, 1500, 7, MonsterType.General, false, false
                , "War Stomp: Deal 400 damage to all enemy monster cards and player", "",
                "Last Order: Increase all friendly monster cards’ AP by 300"
                , ":-HP(400)=emon-eply**:+AP(300)=fmon", ""));
    }

    private static void initVampiricCard() {

        // Vampiric Monster:
        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Undead", 200, 400, 1, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Giant Bat", 500, 900, 3, MonsterType.Normal, false, true
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Stout Undead", 1200, 600, 4, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Undead Mage", 800, 1000, 6, MonsterType.SpellCaster, false, false
                , "", "Curse: Reduce an enemy monster card’s AP by 500", ""
                , "*:-AP(500)?emon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Vampire Acolyte", 1500, 1500, 7, MonsterType.SpellCaster, false, true
                , "", "Black Wave: Deal 300 damage to all enemy monster cards and heal all friendly monster cards for 300 HP", ""
                , "*:-HP(300)=emon:+HP(300)=fmon*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Vampiric, "Vampire Prince", 2000, 2200, 9, MonsterType.General, false, false
                , "Fear: Return two random enemy monster cards from field to hand", "",
                "Darkness: Reduce all enemy monster card's AP by 200 and increase friendly monster cards’ AP by 200"
                , ":HND;emon:HND;emon**:-AP(200)=emon:+AP(200)=fmon", ""));

    }

    private static void initDemonicCard() {


        // Demonic Monster:
        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Imp", 300, 500, 2, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Shade", 500, 800, 3, MonsterType.Normal, false, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Living Armor", 1500, 400, 5, MonsterType.Normal, true, false
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Hellhound", 800, 1000, 5, MonsterType.Normal, false, true
                , "", "", ""
                , "", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Evil Eye", 400, 400, 6, MonsterType.SpellCaster, false, false
                , "", "Evil Gaze: Deal 800 damage to all enemy monster cards and player", ""
                , "*:-HP(800)=emon-eply*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Necromancer", 1200, 1500, 7, MonsterType.SpellCaster, false, true
                , "", "Raise Dead: Move a random card from your graveyard to hand", ""
                , "*:GHND;fmon-fspl*", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "Dark Knight", 2500, 2500, 8, MonsterType.General, false, false
                , "Sacrifice: Select and move a card from hand to graveyard", ""
                , "Loyalty: Heal your player for 1000 HP"
                , ":FUNC1**+HP(1000)=fply", ""));

        addCardToList(new MonsterCard(MonsterTribe.Demonic, "CerberusGOH", 3000, 2000, 10, MonsterType.Hero, false, true
                , "Open the Gate: Draw three cards from deck to hand"
                , "Hellfire: Deal 300 damage to all enemy monster cards and Increase HP and AP of all friendly monster cards by 300"
                , "Revenge of the Two Heads: Send two random enemy monster cards from field to graveyard"
                , ":DHND:DHND:DHND*:-HP(300)=emon:+HP(300)=fmon:+AP(300)=fmon*:GR;emon:GR;emon", ""));
    }

    private static void addCardToList(Card card) {
        cardList.put(card.getName(), card);
    }

    private static void addItemToList(Item item) {
        itemList.put(item.getName(), item);
    }

    private static void addAmuletToList(Amulet amulet) {
        amuletList.put(amulet.getName(), amulet);
    }

    public static Map<String, Card> getCardList() {
        return cardList;
    }

    public static Map<String, Item> getItemList() {
        return itemList;
    }

    public static Map<String, Amulet> getAmuletList() {
        return amuletList;
    }

    public static void setCardList(Map<String, Card> cardList) {
        Initializer_Statics.cardList = cardList;
    }

    public static void setItemList(Map<String, Item> itemList) {
        Initializer_Statics.itemList = itemList;
    }

    public static void setAmuletList(Map<String, Amulet> amuletList) {
        Initializer_Statics.amuletList = amuletList;
    }
}
